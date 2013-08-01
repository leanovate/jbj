package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{FloatVal, NumericVal}
import de.leanovate.jbj.ast.expr.BinaryExpr

case class DivExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(ctx: Context) = left.eval(ctx).toNum / right.eval(ctx).toNum
}
