package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context

case class TernaryExpr(cond: Expr, trueExpr: Expr, falseExpr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = if (cond.eval.toBool.asBoolean)
    trueExpr.eval
  else
    falseExpr.eval
}
