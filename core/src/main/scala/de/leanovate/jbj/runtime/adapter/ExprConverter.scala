package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PAnyVal
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.expr.value.ScalarExpr

object ExprConverter extends Converter[Expr, PAnyVal] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = expr

  def toScala(value: PAnyVal)(implicit ctx: Context) = ScalarExpr(value)

  def toJbj(expr: Expr)(implicit ctx: Context) = expr.eval
}
