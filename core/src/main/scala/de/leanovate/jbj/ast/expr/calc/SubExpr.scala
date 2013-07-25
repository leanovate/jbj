package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{FloatVal, NumericVal, IntegerVal}

case class SubExpr(left: Expr, right: Expr) extends Expr {
  def position = left.position

  def eval(ctx: Context) = (left.eval(ctx).toNum, right.eval(ctx).toNum) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => IntegerVal(leftVal - rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => FloatVal(leftVal - rightVal)
  }
}
