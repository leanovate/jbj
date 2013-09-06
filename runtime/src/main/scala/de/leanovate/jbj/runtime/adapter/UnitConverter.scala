/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{NullVal, PVal}
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.PParam

object UnitConverter extends Converter[Unit, PVal] {
  def toScalaWithConversion(param: PParam)(implicit ctx: Context) {}

  def toScala(value: PVal)(implicit ctx: Context) {}

  def toJbj(value: Unit)(implicit ctx: Context) = NullVal
}
