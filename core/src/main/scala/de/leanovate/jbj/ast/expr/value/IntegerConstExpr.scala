package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.value.IntegerVal

case class IntegerConstExpr(value: Int) extends Expr {
  def eval(ctx: Context) = IntegerVal(value)
}
