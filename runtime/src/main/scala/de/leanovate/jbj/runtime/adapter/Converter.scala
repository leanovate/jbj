/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.PParam

trait Converter[ScalaType, JbjType <: PAny] {
  def toScalaWithConversion(expr: PParam)(implicit ctx: Context): ScalaType

  def toScala(value: JbjType)(implicit ctx: Context): ScalaType

  def toJbj(value: ScalaType)(implicit ctx: Context): JbjType
}
