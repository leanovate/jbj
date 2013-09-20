/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins

import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.value.{DoubleVal, ArrayVal, PVal}
import de.leanovate.jbj.runtime.context.Context

object ValueFunctions {
  @GlobalFunction
  def is_null(value: PVal): Boolean = value.isNull

  @GlobalFunction
  def is_array(value: PVal): Boolean = value.concrete match {
    case _: ArrayVal => true
    case _ => false
  }

  @GlobalFunction
  def is_float(value: PVal): Boolean = value.concrete match {
    case _: DoubleVal => true
    case _ => false
  }

  @GlobalFunction
  def gettype(value: PVal): String = value.typeName

  @GlobalFunction
  def defined(name: String)(implicit ctx: Context): Boolean = ctx.global.findConstant(name).isDefined
}
