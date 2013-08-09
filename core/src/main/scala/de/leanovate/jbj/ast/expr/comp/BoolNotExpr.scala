package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.BooleanVal

case class BoolNotExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = BooleanVal(!expr.eval.toBool.asBoolean)
}
