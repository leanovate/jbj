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
  override def typeName = "integer"

  override def missingValue(implicit ctx: Context) = 0

  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = pAny.asVal.toInteger.asInt

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = param.byVal.toInteger.asInt

  override def toScala(value: PAny)(implicit ctx: Context) = value.asVal.concrete match {
    case IntegerVal(i) => Some(i.toInt)
    case _ => None
  }

  override def toJbj(value: Int)(implicit ctx: Context) = IntegerVal(value)
}
