package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, StringVal}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.Expr

object ByteArrayConverter extends Converter[Array[Byte], StringVal] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = expr.eval.toStr.chars

  def toScala(value: StringVal)(implicit ctx: Context) = value.chars

  def toJbj(value: Array[Byte])(implicit ctx: Context) = StringVal(value)
}
