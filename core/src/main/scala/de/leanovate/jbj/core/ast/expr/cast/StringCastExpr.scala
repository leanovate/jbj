/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.cast

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context

case class StringCastExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = expr.eval.asVal.toStr

  override def phpStr = "(string)" + expr.phpStr
}
