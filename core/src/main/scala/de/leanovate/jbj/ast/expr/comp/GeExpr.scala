package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.runtime.value.BooleanVal
import de.leanovate.jbj.runtime.{Value, Context}
import de.leanovate.jbj.ast.Expr

case class GeExpr(left: Expr, right: Expr) extends Expr {
  def eval(ctx: Context) = BooleanVal(Value.compare(left.eval(ctx), right.eval(ctx)) >= 0)
}
