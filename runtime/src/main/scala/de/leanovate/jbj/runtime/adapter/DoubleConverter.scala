/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, DoubleVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam

object DoubleConverter extends Converter[Double, DoubleVal] {
  override def typeName = "double"

  override def missingValue(implicit ctx: Context) = 0.0

  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = pAny.asVal.toDouble.asDouble

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = param.byVal.toDouble.asDouble


  override def toScala(value: PAny)(implicit ctx: Context) = value.asVal.concrete match {
    case DoubleVal(d) => Some(d)
    case _ => None
  }

  override def toJbj(value: Double)(implicit ctx: Context) = DoubleVal(value)
}
