package controllers

import de.leanovate.jbj.Locator
import java.net.JarURLConnection
import java.io.File
import play.api.Play
import play.api.Play.current
import scala.io.Source

object PlayLocator extends Locator {
  def getETag(fileName: String) = {
    val resource = Play.resource(fileName)

    resource.flatMap {
      case url if new File(url.getFile).isDirectory =>
        None
      case url =>
        lastModifiedFor(url)
    }
  }

  def readScript(fileName: String) = {
    val resource = Play.resource(fileName)

    resource.flatMap {
      case url if new File(url.getFile).isDirectory =>
        None
      case url =>
        lastModifiedFor(url).map {
          lastModified =>
            Script(Source.fromInputStream(url.openStream()).mkString, lastModified)
        }
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
