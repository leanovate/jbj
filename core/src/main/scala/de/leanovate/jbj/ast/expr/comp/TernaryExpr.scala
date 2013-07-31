package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context

case class TernaryExpr(cond: Expr, trueExpr: Expr, falseExpr: Expr) extends Expr {
  def eval(ctx: Context) = if (cond.eval(ctx).toBool.value)
    trueExpr.eval(ctx)
  else
    falseExpr.eval(ctx)
}
