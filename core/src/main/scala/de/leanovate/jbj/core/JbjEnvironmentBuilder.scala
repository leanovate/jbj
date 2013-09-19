/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core

import java.io.PrintStream
import de.leanovate.jbj.api.http.JbjEnvironment

case class JbjEnvironmentBuilder() extends JbjEnvironment.Builder {
  def build() = JbjEnv(scriptLocator, settings, Seq.empty, Option(errorStream).map(new PrintStream(_, false, "UTF-8")))
}
