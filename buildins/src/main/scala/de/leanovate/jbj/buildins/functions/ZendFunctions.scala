/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.adapter.GlobalFunctions

trait ZendFunctions {
  @GlobalFunction
  def zend_version(): String = "2.5.0"
}

object ZendFunctions extends ZendFunctions {
  val functions = GlobalFunctions.generatePFunctions(this)
}