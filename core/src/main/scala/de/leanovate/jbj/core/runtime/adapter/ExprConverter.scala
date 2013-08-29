/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.PVal
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.core.runtime.context.Context

object ExprConverter extends Converter[Expr, PVal] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = expr

  def toScala(value: PVal)(implicit ctx: Context) = ScalarExpr(value)

  def toJbj(expr: Expr)(implicit ctx: Context) = expr.eval.asVal
}
