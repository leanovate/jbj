package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.Value
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.expr.value.ScalarExpr

object ExprConverter extends Converter[Expr, Value] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = expr

  def toScala(value: Value)(implicit ctx: Context) = ScalarExpr(value)

  def toJbj(expr: Expr)(implicit ctx: Context) = expr.eval
}
