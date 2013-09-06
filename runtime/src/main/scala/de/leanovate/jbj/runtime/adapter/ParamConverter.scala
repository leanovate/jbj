/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.{PValParam, PParam}

object ParamConverter extends Converter[PParam, PVal] {
  def toScalaWithConversion(param: PParam)(implicit ctx: Context) = param

  def toScala(value: PVal)(implicit ctx: Context) = PValParam(value)

  def toJbj(param: PParam)(implicit ctx: Context) = param.byVal
}
