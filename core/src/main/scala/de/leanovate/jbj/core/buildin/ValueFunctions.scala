/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.buildin

import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.value.{ArrayVal, PVal}

object ValueFunctions {
  @GlobalFunction
  def is_null(value: PVal): Boolean = value.isNull

  @GlobalFunction
  def is_array(value: PVal): Boolean = value.concrete match {
    case _: ArrayVal => true
    case _ => false
  }
}
