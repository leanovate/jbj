package de.leanovate.jbj.core.ast.expr.comp

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

case class TernaryExpr(cond: Expr, trueExpr: Expr, falseExpr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = if (cond.eval.asVal.toBool.asBoolean)
    trueExpr.eval
  else
    falseExpr.eval
}
