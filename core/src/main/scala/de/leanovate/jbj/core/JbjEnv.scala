/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core

import de.leanovate.jbj.runtime.{JbjRuntimeEnv, PInterface, PFunction, PClass}
import java.io.{OutputStream, PrintStream}
import de.leanovate.jbj.runtime.context.Context
import scala.collection.JavaConverters._
import scala.collection.Map
import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.api._
import de.leanovate.jbj.runtime.exception.{ExitJbjException, NotFoundJbjException}
import de.leanovate.jbj.runtime.env.{CliEnvironment, CgiEnvironment}
import scala.Some
import de.leanovate.jbj.runtime.context.GlobalContext
import de.leanovate.jbj.runtime.output.OutputBuffer
import de.leanovate.jbj.core.ast.Prog
import de.leanovate.jbj.core.parser.{ParseContext, JbjParser}
import de.leanovate.jbj.api.http.{Response, RequestInfo}

case class JbjEnv(locator: JbjScriptLocator = new DefaultJbjScriptLocator,
                  settings: JbjSettings = new JbjSettings,
                  extensions: Seq[JbjExtension] = Seq.empty,
                  errorStream: Option[PrintStream] = None) extends JbjEnvironment with JbjRuntimeEnv {

  case class CacheEntry(etag: String, entry: Either[Prog, Throwable])

  private val cache = new java.util.concurrent.ConcurrentHashMap[String, CacheEntry]().asScala

  val preedfinedConstants: Map[String, PVal] =
    (extensions.flatMap(_.constants) ++ buildin.buildinConstants).toMap

  val predefinedFunctions: Map[Seq[String], PFunction] =
    (extensions.flatMap(_.function) ++ buildin.buildinFunctions).map {
      function => function.name.lowercase -> function
    }.toMap

  val predefinedInterfaces: Map[Seq[String], PInterface] =
    Map.empty

  val predefinedClasses: Map[Seq[String], PClass] =
    (extensions.flatMap(_.classes) ++ buildin.buildinClasses).map {
      c =>
        c.name.lowercase -> c
    }.toMap

  def newGlobalContext(out: OutputStream) = {
    val contextSettings = settings.clone()
    GlobalContext(this, OutputBuffer(out, contextSettings), errorStream, contextSettings)
  }

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
    implicit val ctx: Context = newGlobalContext(output)

    runImpl(phpScript)
  }

  override def run(phpScript: String, args: Array[String], output: OutputStream) {
    implicit val ctx: Context = newGlobalContext(output)

    CliEnvironment.commandLine(phpScript, args)
    runImpl(phpScript)
  }

  override def run(phpScript: String, request: RequestInfo, response: Response) {
    implicit val ctx: Context = newGlobalContext(response.getOutputStream)

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
    } catch {
      case e: ExitJbjException =>
        ctx.out.print(e.message)
    } finally {
      ctx.cleanup()
    }
  }
}
