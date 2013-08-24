package de.leanovate.jbj.core.ast.expr.calc

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

case class NegExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = -expr.eval
}
