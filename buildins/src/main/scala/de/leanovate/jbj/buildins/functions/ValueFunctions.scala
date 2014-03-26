/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.value.{DoubleVal, ArrayVal, PVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.adapter.GlobalFunctions

trait ValueFunctions {
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
  def gettype(value: PVal): String = value.typeName(simple = false)
}

object ValueFunctions extends ValueFunctions {
  val functions = GlobalFunctions.generatePFunctions[ValueFunctions]
}