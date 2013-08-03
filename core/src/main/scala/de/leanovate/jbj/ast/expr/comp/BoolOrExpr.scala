package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.BooleanVal
import de.leanovate.jbj.ast.expr.BinaryExpr

case class BoolOrExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(implicit ctx: Context) = {
    if (left.eval.toBool.value)
      BooleanVal.TRUE
    else
      right.eval.toBool
  }
}
