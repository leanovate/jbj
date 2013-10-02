/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, PVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.{PAnyParam, PValParam, PParam}

object ParamConverter extends Converter[PParam, PVal] {
  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = PAnyParam(pAny)

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = param

  override def toScala(value: PVal)(implicit ctx: Context) = PAnyParam(value)

  override def toJbj(param: PParam)(implicit ctx: Context) = param.byVal
}
