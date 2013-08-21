package de.leanovate.jbj.ast.expr.cast

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context

case class BooleanCastExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = expr.evalOld.toBool
}
