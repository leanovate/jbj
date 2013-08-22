package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

object ByteArrayConverter extends Converter[Array[Byte], StringVal] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = expr.eval.asVal.toStr.chars

  def toScala(value: StringVal)(implicit ctx: Context) = value.chars

  def toJbj(value: Array[Byte])(implicit ctx: Context) = StringVal(value)
}
