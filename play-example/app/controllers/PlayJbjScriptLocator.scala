package controllers

import java.net.JarURLConnection
import java.io.{IOException, File}
import play.api.{Logger, Play}
import play.api.Play.current
import scala.io.Source
import de.leanovate.jbj.api.http.JbjScriptLocator

object PlayJbjScriptLocator extends JbjScriptLocator {
  def getETag(fileName: String) = {
    val resource = Play.resource(fileName)

    resource.flatMap {
      case url if new File(url.getFile).isDirectory =>
        None
      case url =>
        lastModifiedFor(url)
    }.orNull
  }

  def readScript(fileName: String) = {
    val resource = Play.resource(fileName)

    try {
      resource.flatMap {
        case url if new File(url.getFile).isDirectory =>
          None
        case url =>
          lastModifiedFor(url).map {
            lastModified =>
              new JbjScriptLocator.Script(fileName, lastModified, Source.fromInputStream(url.openStream()).mkString)
          }
      }.orNull
    } catch {
      case e: IOException =>
        Logger.error(s"Failed to read $fileName", e)
        throw new RuntimeException(e)
    }
  }

  private def lastModifiedFor(resource: java.net.URL): Option[String] = resource.getProtocol match {
    case "file" => Some(new java.io.File(resource.getPath).lastModified.toString)
    case "jar" => {
      resource.getPath.split('!').drop(1).headOption.flatMap {
        fileNameInJar =>
          Option(resource.openConnection)
            .collect {
            case c: JarURLConnection => c
          }.flatMap(c => Option(c.getJarFile.getJarEntry(fileNameInJar.drop(1))))
            .map(_.getTime)
            .filterNot(_ == 0)
            .map(lastModified => lastModified.toString)
      }
    }
    case _ => None
  }

}
