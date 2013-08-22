package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.ast.expr.BinaryExpr
import de.leanovate.jbj.runtime.context.Context

case class ConcatExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(implicit ctx: Context) = left.eval __ right.eval
}
