package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.exec.Context
import de.leanovate.jbj.ast.value.{FloatVal, NumericVal, IntegerVal}

case class NegExpr(expr: Expr) extends Expr {
  def eval(ctx: Context) = expr.eval(ctx).toNumeric match {
    case IntegerVal(value) => IntegerVal(-value)
    case NumericVal(value) => FloatVal(-value)
  }
}
