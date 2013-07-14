package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.exec.Context
import de.leanovate.jbj.ast.value.{FloatVal, NumericVal, IntegerVal}

case class SubExpr(left: Expr, right: Expr) extends Expr {
  def eval(ctx: Context) = (left.eval(ctx).toNumeric, right.eval(ctx).toNumeric) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => IntegerVal(leftVal - rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => FloatVal(leftVal - rightVal)
  }
}
