/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests

import scala.io.Source
import de.leanovate.jbj.api.http.JbjScriptLocator

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
      TestLocator.getClass.getResource(fileName.substring(1))
    else
      TestLocator.getClass.getResource(fileName)
    ).map {
      url =>
        new JbjScriptLocator.Script(fileName, url.toString, Source.fromInputStream(url.openStream()).mkString)
    }.orNull
  }
}
