/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context

object IntConverter extends Converter[Int, IntegerVal] {

  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = toScala(expr.eval.asVal.toInteger)

  def toScala(value: IntegerVal)(implicit ctx: Context) = value.asInt

  def toJbj(value: Int)(implicit ctx: Context) = IntegerVal(value)
}
