/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins

import de.leanovate.jbj.runtime.annotations.GlobalFunction

object EnvironmentFunctions {
  @GlobalFunction
  def zend_version(): String = "2.5.0"
}
