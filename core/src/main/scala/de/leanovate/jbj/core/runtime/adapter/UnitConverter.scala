/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.{NullVal, PVal}
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

object UnitConverter extends Converter[Unit, PVal] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) {}

  def toScala(value: PVal)(implicit ctx: Context) {}

  def toJbj(value: Unit)(implicit ctx: Context) = NullVal
}
