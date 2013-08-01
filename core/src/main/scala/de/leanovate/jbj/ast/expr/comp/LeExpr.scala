package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{Value, Context}
import de.leanovate.jbj.runtime.value.BooleanVal
import de.leanovate.jbj.ast.expr.BinaryExpr

case class LeExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(ctx: Context) = BooleanVal(Value.compare(left.eval(ctx), right.eval(ctx)) <= 0)
}
