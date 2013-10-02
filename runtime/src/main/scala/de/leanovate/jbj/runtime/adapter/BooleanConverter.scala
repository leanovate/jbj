/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, BooleanVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam

object BooleanConverter extends Converter[Boolean, BooleanVal] {
  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = toScala(pAny.asVal.toBool)

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = toScala(param.byVal.toBool)

  override def toScala(value: BooleanVal)(implicit ctx: Context) = value.asBoolean

  override def toJbj(value: Boolean)(implicit ctx: Context) = BooleanVal(value)
}
