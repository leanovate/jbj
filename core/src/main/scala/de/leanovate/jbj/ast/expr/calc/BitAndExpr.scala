package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{StringVal, NumericVal, IntegerVal}
import de.leanovate.jbj.ast.expr.BinaryExpr

case class BitAndExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(implicit ctx: Context) = (left.evalOld, right.evalOld) match {
    case (leftVal: StringVal, rightVal:StringVal) => leftVal & rightVal
    case (leftVal, rightVal) => leftVal.toInteger & rightVal.toInteger
  }
}