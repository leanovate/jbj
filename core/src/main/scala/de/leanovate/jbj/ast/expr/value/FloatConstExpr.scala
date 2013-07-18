package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.value.FloatVal

case class FloatConstExpr(value: Double) extends Expr {
  def eval(ctx: Context) = FloatVal(value)
}
