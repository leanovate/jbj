/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam

trait Converter[ScalaType, JbjType <: PAny] {
  def typeName: String

  def missingValue(implicit ctx: Context): ScalaType

  def toScalaWithConversion(pAny: PAny)(implicit ctx: Context): ScalaType

  def toScalaWithConversion(param: PParam)(implicit ctx: Context): ScalaType

  def toScala(param:PParam)(implicit ctx:Context):Option[ScalaType] = toScala(param.byVal)

  def toScala(value: PAny)(implicit ctx: Context): Option[ScalaType]

  def toJbj(value: ScalaType)(implicit ctx: Context): JbjType
}
