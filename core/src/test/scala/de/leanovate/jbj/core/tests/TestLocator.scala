package de.leanovate.jbj.core.tests

import scala.io.Source
import de.leanovate.jbj.core.Locator

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
