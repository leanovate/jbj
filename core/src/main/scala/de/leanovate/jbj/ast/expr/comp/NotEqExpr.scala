package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

case class NotEqExpr(left: Expr, right: Expr) extends Expr {
  override def eval(implicit ctx: Context) = left.eval != right.eval
}
