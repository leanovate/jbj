package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.exec.Context
import de.leanovate.jbj.ast.value.{IntegerVal, StringVal}

trait AbstractCompExpr extends Expr {
  def left: Expr

  def right: Expr

  def comp(ctx: Context): Int = (left.eval(ctx), right.eval(ctx)) match {
    case (StringVal(leftVal), StringVal(rightVal)) => leftVal.compare(rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => leftVal.compare(rightVal)
    case (anyLeft, anyRight) => anyLeft.toNum.toDouble.compare(anyRight.toNum.toDouble)
  }
}
