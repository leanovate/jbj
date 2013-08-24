package de.leanovate.jbj.core.ast.expr.cast

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

case class DoubleCastExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = expr.eval.asVal.toDouble
}
