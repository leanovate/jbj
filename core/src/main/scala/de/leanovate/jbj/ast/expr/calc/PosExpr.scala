package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

case class PosExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = expr.evalOld.toNum
}
