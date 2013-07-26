package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.{FilePosition, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.FloatVal

case class FloatConstExpr(position: FilePosition, value: Double) extends Expr {
  lazy val _value = FloatVal(value)

  def eval(ctx: Context) = _value
}
