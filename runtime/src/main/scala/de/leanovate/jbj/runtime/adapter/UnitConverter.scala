/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, NullVal, PVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam

object UnitConverter extends Converter[Unit, PVal] {
  override def typeName = "nothing"

  override def missingValue(implicit ctx: Context) = {}

  override def toScalaWithConversion(param: PAny)(implicit ctx: Context) {}

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) {}

  override def toScala(value: PAny)(implicit ctx: Context) = Some({})

  override def toJbj(value: Unit)(implicit ctx: Context) = NullVal
}
