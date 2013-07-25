package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.{FilePosition, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.IntegerVal

case class IntegerConstExpr(position: FilePosition, value: Long) extends Expr {
  lazy val _value = IntegerVal(value)

  def eval(ctx: Context) = _value
}
