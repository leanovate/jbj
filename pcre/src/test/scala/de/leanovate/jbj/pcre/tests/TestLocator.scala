package de.leanovate.jbj.pcre.tests

import de.leanovate.jbj.api.http.JbjScriptLocator
import scala.io.Source

object TestLocator extends JbjScriptLocator {
  def getETag(fileName: String) = {
    Option(if (fileName.startsWith("/"))
      TestLocator.getClass.getResource(fileName.substring(1))
    else
      TestLocator.getClass.getResource(fileName)
          ).map(_.toString).orNull
  }

  def readScript(fileName: String) = {
    Option(if (fileName.startsWith("/"))
      TestLocator.getClass.getResource(fileName.substring(1).toLowerCase)
    else
      TestLocator.getClass.getResource(fileName.toLowerCase)
          ).map {
      url =>
        new JbjScriptLocator.Script(fileName.toLowerCase, url.toString, Source.fromInputStream(url.openStream()).mkString)
    }.orNull
  }
}
