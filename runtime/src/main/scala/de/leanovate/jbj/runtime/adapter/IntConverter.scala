/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.PParam

object IntConverter extends Converter[Int, IntegerVal] {

  def toScalaWithConversion(param: PParam)(implicit ctx: Context) = toScala(param.byVal.toInteger)

  def toScala(value: IntegerVal)(implicit ctx: Context) = value.asInt

  def toJbj(value: Int)(implicit ctx: Context) = IntegerVal(value)
}
