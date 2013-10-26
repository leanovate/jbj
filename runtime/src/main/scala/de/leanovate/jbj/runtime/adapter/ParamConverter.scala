/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{NullVal, PAny, PVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.{PAnyParam, PValParam, PParam}

object ParamConverter extends Converter[PParam, PVal] {
  override def typeName = "any"

  override def missingValue(implicit ctx: Context) = PAnyParam(NullVal)

  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = PAnyParam(pAny)

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = param

  override def toScala(value: PAny)(implicit ctx: Context) = Some(PAnyParam(value))

  override def toJbj(param: PParam)(implicit ctx: Context) = param.byVal
}
