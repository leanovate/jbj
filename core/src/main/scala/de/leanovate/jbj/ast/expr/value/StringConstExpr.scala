package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.{FilePosition, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.StringVal

case class StringConstExpr(position: FilePosition, value: String) extends Expr {
  lazy val _value = StringVal(value)

  def eval(ctx: Context) = _value
}
