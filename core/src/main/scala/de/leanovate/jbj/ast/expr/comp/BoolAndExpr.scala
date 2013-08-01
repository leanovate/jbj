package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.BooleanVal
import de.leanovate.jbj.ast.expr.BinaryExpr

case class BoolAndExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(ctx: Context) = {
    if (!left.eval(ctx).toBool.value)
      BooleanVal.FALSE
    else
      right.eval(ctx).toBool
  }
}
