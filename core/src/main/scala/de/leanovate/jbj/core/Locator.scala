package de.leanovate.jbj.core

import scala.io.Source
import java.io.File

trait Locator {

  case class Script(content: String, fileName:String, etag: String)

  def getETag(fileName: String): Option[String]

  def readScript(fileName: String): Option[Script]
}

object DefaultLocator extends Locator {
  def getETag(fileName: String) = {
    val file = new File(fileName)

    if (file.isFile && file.canRead)
      Some(file.lastModified.toString)
    else
      None
  }

  def readScript(fileName: String) = {
    val file = new File(fileName)

    if (file.isFile && file.canRead)
      Some(Script(Source.fromFile(file).mkString, file.getAbsolutePath, file.lastModified.toString))
    else
      None
  }
}