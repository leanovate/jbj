package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

object StringConverter extends Converter[String, StringVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = toScala(expr.eval.asVal.toStr)

  override def toScala(value: StringVal)(implicit ctx: Context) = value.asString

  override def toJbj(value: String)(implicit ctx: Context) = StringVal(value)
}
