package de.leanovate.jbj

import de.leanovate.jbj.ast.Prog
import de.leanovate.jbj.runtime.{PFunction, PClass, buildin, Settings}
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.GlobalContext
import de.leanovate.jbj.parser.{JbjParser, ParseContext}
import scala.collection.JavaConverters._
import scala.collection.Map
import de.leanovate.jbj.runtime.value.PVal

case class JbjEnv(locator: Locator = DefaultLocator, extensions: Seq[JbjExtension] = Seq.empty) {

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

  def newGlobalContext(out: PrintStream, err: PrintStream) = GlobalContext(this, out, err, settings.clone)

  def parse(fileName: String): Option[Either[Prog, Throwable]] = cache.get(fileName) match {
    case Some(cacheEntry) if locator.getETag(fileName).exists(_ == cacheEntry.etag) =>
      Some(cacheEntry.entry)
    case Some(_) if !locator.getETag(fileName).isDefined =>
      cache.remove(fileName)
      None
    case None => locator.readScript(fileName).map {
      script =>
        val parser = new JbjParser(ParseContext(script.fileName, settings))
        val result = try {
          Left(parser.parse(script.content))
        } catch {
          case e: Throwable => Right(e)
        }
        cache.put(fileName, CacheEntry(script.etag, result))
        result
    }
  }
}
