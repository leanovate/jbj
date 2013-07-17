package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.{Value, Expr}
import de.leanovate.jbj.exec.Context
import de.leanovate.jbj.ast.value._

case class GtExpr(left: Expr, right: Expr) extends Expr {
  def eval(ctx: Context) = BooleanVal(Value.compare(left.eval(ctx), right.eval(ctx)) > 0)
}
