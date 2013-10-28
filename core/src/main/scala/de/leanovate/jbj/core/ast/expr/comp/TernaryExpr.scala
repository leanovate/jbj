/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.comp

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context

case class TernaryExpr(cond: Expr, trueExpr: Option[Expr], falseExpr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    val condVal = cond.eval.asVal
    if (condVal.toBool.asBoolean)
      trueExpr.map(_.eval).getOrElse(condVal)
    else
      falseExpr.eval
  }

  override def phpStr = cond.phpStr + "?" + trueExpr.map(_.phpStr).getOrElse("") + ":" + falseExpr.phpStr
}
