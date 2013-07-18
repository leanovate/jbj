package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.value.BooleanVal

case class BoolOrExpr(left: Expr, right: Expr) extends Expr {
  def eval(ctx: Context) = {
    if (left.eval(ctx).toBool.value)
      BooleanVal(true)
    else
      right.eval(ctx).toBool
  }
}
