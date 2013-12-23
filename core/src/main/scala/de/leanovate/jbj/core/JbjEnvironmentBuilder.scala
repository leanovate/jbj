/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core

import java.io.PrintStream
import de.leanovate.jbj.api.http.JbjEnvironment
import scala.collection.JavaConversions._
import de.leanovate.jbj.buildins.{ZendExtension, StandardExtension}

case class JbjEnvironmentBuilder() extends JbjEnvironment.Builder[JbjEnv] {
  def build() =
    JbjEnv(scriptLocator, settings, fileSystem, StandardExtension +: ZendExtension +: extensions.toSeq, Option(errorStream).map {
      err =>
        new PrintStream(err, false, "UTF-8")
    })
}
