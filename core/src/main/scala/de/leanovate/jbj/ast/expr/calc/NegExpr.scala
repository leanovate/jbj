package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{FilePosition, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{FloatVal, NumericVal, IntegerVal}

case class NegExpr(position: FilePosition, expr: Expr) extends Expr {
  def eval(ctx: Context) = expr.eval(ctx).toNum.neg
}
