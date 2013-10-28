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

object LongConverter extends Converter[Long, IntegerVal] {
  override def typeName = "long"

  override def missingValue(implicit ctx: Context) = 0L

  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = pAny.asVal.toInteger.asLong

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = param.byVal.toInteger.asLong

  override def toScala(value: PAny)(implicit ctx: Context) = value.asVal.concrete match {
    case IntegerVal(i) => Some(i)
    case _ => None
  }

  override def toJbj(value: Long)(implicit ctx: Context) = IntegerVal(value)
}
