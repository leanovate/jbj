package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{FloatVal, NumericVal, IntegerVal}

case class MulExpr(left: Expr, right: Expr) extends Expr {
  override def eval(ctx: Context) = (left.eval(ctx).toNum, right.eval(ctx).toNum) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal == 0 => IntegerVal(0)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal > 0 && leftVal <= Long.MaxValue / rightVal && leftVal >= Long.MinValue / rightVal => IntegerVal(leftVal * rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal < 0 && leftVal <= Long.MinValue / rightVal && leftVal >= Long.MaxValue / rightVal => IntegerVal(leftVal * rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => FloatVal(leftVal * rightVal)
  }
}
