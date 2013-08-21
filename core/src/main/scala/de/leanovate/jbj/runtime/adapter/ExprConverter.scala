package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.ast.expr.value.ScalarExpr
import de.leanovate.jbj.runtime.context.Context

object ExprConverter extends Converter[Expr, PVal] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = expr

  def toScala(value: PVal)(implicit ctx: Context) = ScalarExpr(value)

  def toJbj(expr: Expr)(implicit ctx: Context) = expr.evalOld
}
