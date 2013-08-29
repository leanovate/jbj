/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core

import de.leanovate.jbj.api.JbjEnvironment
import java.io.PrintStream

case class JbjEnvironmentBuilder() extends JbjEnvironment.Builder {
  def build() = JbjEnv(scriptLocator, settings, Seq.empty, Option(errorStream).map(new PrintStream(_, false, "UTF-8")))
}
