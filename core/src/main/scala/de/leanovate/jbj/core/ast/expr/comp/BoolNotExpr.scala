package de.leanovate.jbj.core.ast.expr.comp

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.value.BooleanVal
import de.leanovate.jbj.core.runtime.context.Context

case class BoolNotExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = BooleanVal(!expr.eval.asVal.toBool.asBoolean)
}
