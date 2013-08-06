package de.leanovate.jbj

import de.leanovate.jbj.ast.Prog
import de.leanovate.jbj.runtime.Settings
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.GlobalContext
import de.leanovate.jbj.parser.{JbjParser, ParseContext}
import scala.collection.JavaConverters._

case class JbjEnv(locator: Locator = DefaultLocator) {

  case class CacheEntry(etag: String, entry: Either[Prog, Throwable])

  private val cache = new java.util.concurrent.ConcurrentHashMap[String, CacheEntry]().asScala

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
        val parser = new JbjParser(ParseContext(fileName))
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
