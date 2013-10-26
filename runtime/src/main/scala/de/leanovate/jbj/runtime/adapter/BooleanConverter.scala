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
  override def typeName = "boolean"

  override def missingValue(implicit ctx: Context) = false

  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = pAny.asVal.toBool.asBoolean

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = param.byVal.toBool.asBoolean

  override def toScala(value: PAny)(implicit ctx: Context) = value.asVal.concrete match {
    case BooleanVal(bool) => Some(bool)
    case _ => None
  }

  override def toJbj(value: Boolean)(implicit ctx: Context) = BooleanVal(value)
}
