package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.value.BooleanVal
import de.leanovate.jbj.runtime.context.Context

case class BoolNotExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = BooleanVal(!expr.evalOld.toBool.asBoolean)
}
