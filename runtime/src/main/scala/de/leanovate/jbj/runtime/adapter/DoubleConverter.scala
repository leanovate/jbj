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
  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = toScala(pAny.asVal.toDouble)

  override def toScalaWithConversion(param:PParam)(implicit ctx: Context) = toScala(param.byVal.toDouble)

  override def toScala(value: DoubleVal)(implicit ctx: Context) = value.asDouble

  override def toJbj(value: Double)(implicit ctx: Context) = DoubleVal(value)
}
