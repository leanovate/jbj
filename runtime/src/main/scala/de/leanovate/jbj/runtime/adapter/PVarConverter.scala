/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, PVar}
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.context.Context

object PVarConverter extends Converter[PVar, PVar] {
  override def typeName = "any"

  override def missingValue(implicit ctx: Context) = PVar()

  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = pAny.asVar

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = param.byRef.map(_.asVar).getOrElse(PVar(param.byVal))

  override def toScala(param: PParam)(implicit ctx: Context): Option[PVar] = param.byRef.map(_.asVar)

  override def toScala(value: PAny)(implicit ctx: Context) = Some(value.asVar)

  override def toJbj(value: PVar)(implicit ctx: Context) = value
}