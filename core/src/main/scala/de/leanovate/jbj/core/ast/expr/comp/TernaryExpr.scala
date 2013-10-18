/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.comp

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context

case class TernaryExpr(cond: Expr, trueExpr: Expr, falseExpr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = if (cond.eval.asVal.toBool.asBoolean)
    trueExpr.eval
  else
    falseExpr.eval

  override def phpStr = cond.phpStr + "?" + trueExpr.phpStr + ":" + falseExpr.phpStr
}
