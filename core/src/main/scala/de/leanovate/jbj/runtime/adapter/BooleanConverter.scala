package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.BooleanVal
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

object BooleanConverter extends Converter[Boolean, BooleanVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = toScala(expr.evalOld.toBool)

  override def toScala(value: BooleanVal)(implicit ctx: Context) = value.asBoolean

  def toJbj(value: Boolean)(implicit ctx: Context) = BooleanVal(value)
}
