package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{NodePosition, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{FloatVal, NumericVal, IntegerVal}

case class NegExpr(expr: Expr) extends Expr {
  override def eval(ctx: Context) = expr.eval(ctx).toNum.neg
}
