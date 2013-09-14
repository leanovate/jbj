/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PVar
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.context.Context

object PVarConverter extends Converter[PVar, PVar] {
  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = param.byRef.asVar

  override def toScala(value: PVar)(implicit ctx: Context) = value

  override def toJbj(value: PVar)(implicit ctx: Context) = value
}