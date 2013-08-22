package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

object StringConverter extends Converter[String, StringVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = toScala(expr.evalOld.toStr)

  override def toScala(value: StringVal)(implicit ctx: Context) = value.asString

  override def toJbj(value: String)(implicit ctx: Context) = StringVal(value)
}
