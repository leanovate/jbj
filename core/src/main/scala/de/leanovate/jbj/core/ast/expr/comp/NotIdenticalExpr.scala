package de.leanovate.jbj.core.ast.expr.comp

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

case class NotIdenticalExpr(left: Expr, right: Expr) extends Expr {
  override def eval(implicit ctx: Context) = left.eval !== right.eval
}
