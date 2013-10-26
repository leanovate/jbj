/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{NullVal, PAny, ObjectVal, PVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam

object PValConverter extends Converter[PVal, PVal] {
  override def typeName = "any"

  override def missingValue(implicit ctx: Context) = NullVal

  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = pAny.asVal

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = param.byVal

  override def toScala(value: PAny)(implicit ctx: Context) = Some(value.asVal)

  override def toJbj(value: PVal)(implicit ctx: Context) = value
}
