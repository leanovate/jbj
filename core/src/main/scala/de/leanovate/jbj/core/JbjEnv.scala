package de.leanovate.jbj.core

import de.leanovate.jbj.core.ast.Prog
import de.leanovate.jbj.core.runtime.{PFunction, PClass, buildin, Settings}
import java.io.{OutputStream, PrintStream}
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.parser.JbjParser
import scala.collection.JavaConverters._
import scala.collection.Map
import de.leanovate.jbj.core.runtime.value.PVal
import de.leanovate.jbj.api._
import de.leanovate.jbj.core.runtime.exception.NotFoundJbjException
import de.leanovate.jbj.core.runtime.env.{CliEnvironment, CgiEnvironment}
import de.leanovate.jbj.core.parser.ParseContext
import scala.Some
import de.leanovate.jbj.core.runtime.context.GlobalContext

case class JbjEnv(locator: JbjScriptLocator = new DefaultJbjScriptLocator,
                  extensions: Seq[JbjExtension] = Seq.empty,
                  errorStream: Option[PrintStream] = None) extends JbjEnvironment {

  case class CacheEntry(etag: String, entry: Either[Prog, Throwable])

  private val cache = new java.util.concurrent.ConcurrentHashMap[String, CacheEntry]().asScala

  val preedfinedConstants: Map[String, PVal] =
    (extensions.flatMap(_.constants) ++ buildin.buildinConstants).toMap

  val predefinedFunctions: Map[Seq[String], PFunction] =
    (extensions.flatMap(_.function) ++ buildin.buildinFunctions).map {
      function => function.name.lowercase -> function
    }.toMap

  val predefinedClasses: Map[Seq[String], PClass] =
    (extensions.flatMap(_.classes) ++ buildin.buildinClasses).map {
      c =>
        c.name.lowercase -> c
    }.toMap

  val settings: Settings = new Settings

  def newGlobalContext(out: PrintStream) = GlobalContext(this, out, errorStream, settings.clone)

  def parse(fileName: String): Option[Either[Prog, Throwable]] = cache.get(fileName) match {
    case Some(cacheEntry) if Option(locator.getETag(fileName)).exists(_ == cacheEntry.etag) =>
      Some(cacheEntry.entry)
    case Some(_) if !Option(locator.getETag(fileName)).isDefined =>
      cache.remove(fileName)
      None
    case None => Option(locator.readScript(fileName)).map {
      script =>
        val parser = new JbjParser(ParseContext(script.getFilename, settings))
        val result = try {
          Left(parser.parse(script.getContent))
        } catch {
          case e: Throwable => Right(e)
        }
        cache.put(fileName, CacheEntry(script.getEtag, result))
        result
    }
  }

  override def run(phpScript: String, output: OutputStream) {
    implicit val ctx: Context = newGlobalContext(new PrintStream(output, false, "UTF-8"))

    runImpl(phpScript)
  }

  override def run(phpScript: String, args: Array[String], output: OutputStream) {
    implicit val ctx: Context = newGlobalContext(new PrintStream(output, false, "UTF-8"))

    CliEnvironment.commandLine(phpScript, args)
    runImpl(phpScript)
  }

  override def run(phpScript: String, request: RequestInfo, output: OutputStream) {
    implicit val ctx: Context = newGlobalContext(new PrintStream(output, false, "UTF-8"))

    CgiEnvironment.httpRequest(request)
    runImpl(phpScript)
  }

  private def runImpl(phpScript: String)(implicit ctx: Context) {
    try {
      parse(phpScript) match {
        case Some(Left(prog)) =>
          prog.exec
        case Some(Right(exception: JbjException)) =>
          throw exception
        case Some(Right(exception)) =>
          throw new JbjException("General error", exception)
        case None =>
          throw new NotFoundJbjException(phpScript)
      }
    } finally {
      ctx.cleanup()
    }
  }
}
