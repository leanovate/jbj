package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{NumericVal, FloatVal, IntegerVal}

case class AddExpr(left: Expr, right: Expr) extends Expr {
  def eval(ctx: Context) = (left.eval(ctx).toNum, right.eval(ctx).toNum) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => IntegerVal(leftVal + rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => FloatVal(leftVal + rightVal)
  }
}
