/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, IntegerVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam

object IntConverter extends Converter[Int, IntegerVal] {
  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = toScala(pAny.asVal.toInteger)

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = toScala(param.byVal.toInteger)

  override def toScala(value: IntegerVal)(implicit ctx: Context) = value.asInt

  override def toJbj(value: Int)(implicit ctx: Context) = IntegerVal(value)
}
