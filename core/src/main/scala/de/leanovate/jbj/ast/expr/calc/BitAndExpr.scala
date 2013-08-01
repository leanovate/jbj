package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{NumericVal, IntegerVal}
import de.leanovate.jbj.ast.expr.BinaryExpr

case class BitAndExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(ctx: Context) = (left.eval(ctx).toNum, right.eval(ctx).toNum) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => IntegerVal(leftVal & rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => IntegerVal(leftVal.toLong & rightVal.toLong)
  }
}
