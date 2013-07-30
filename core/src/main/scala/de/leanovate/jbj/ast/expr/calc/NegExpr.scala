package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context

case class NegExpr(expr: Expr) extends Expr {
  override def eval(ctx: Context) = expr.eval(ctx).toNum.neg
}
