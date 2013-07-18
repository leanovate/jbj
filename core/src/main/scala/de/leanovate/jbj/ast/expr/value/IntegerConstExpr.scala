package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.IntegerVal

case class IntegerConstExpr(value: Int) extends Expr {
  lazy val _value = IntegerVal(value)

  def eval(ctx: Context) = _value
}
