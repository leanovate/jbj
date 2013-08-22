package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.value.BooleanVal
import de.leanovate.jbj.runtime.context.Context

case class NotEqExpr(left: Expr, right: Expr) extends Expr {
  override def eval(implicit ctx: Context) = BooleanVal(left.evalOld.compare(right.evalOld) != 0)
}
