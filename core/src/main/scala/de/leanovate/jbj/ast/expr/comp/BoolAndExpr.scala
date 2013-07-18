package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.BooleanVal

case class BoolAndExpr(left: Expr, right: Expr) extends Expr {
  def eval(ctx: Context) = {
    if (!left.eval(ctx).toBool.value)
      BooleanVal.FALSE
    else
      right.eval(ctx).toBool
  }
}
