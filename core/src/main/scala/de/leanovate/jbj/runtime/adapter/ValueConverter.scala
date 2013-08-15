package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.Expr

object ValueConverter extends Converter[Value, Value] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = expr.eval

  override def toScala(value: Value)(implicit ctx: Context) = value

  override def toJbj(value: Value)(implicit ctx: Context) = value
}
