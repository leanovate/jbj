package de.leanovate.jbj.tests

import de.leanovate.jbj.Locator
import scala.io.Source

object TestLocator extends Locator {
  def getETag(fileName: String) = {
    Option(if (fileName.startsWith("/"))
      getClass.getResource(fileName.substring(1))
    else
      getClass.getResource(fileName)
    ).map(_.toString)
  }

  def readScript(fileName: String) = {
    Option(if (fileName.startsWith("/"))
      getClass.getResource(fileName.substring(1))
    else
      getClass.getResource(fileName)
    ).map {
      url => Script(Source.fromInputStream(url.openStream()).mkString, fileName, url.toString)
    }
  }
}
