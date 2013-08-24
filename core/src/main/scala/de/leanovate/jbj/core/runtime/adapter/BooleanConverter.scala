package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.BooleanVal
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

object BooleanConverter extends Converter[Boolean, BooleanVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = toScala(expr.eval.asVal.toBool)

  override def toScala(value: BooleanVal)(implicit ctx: Context) = value.asBoolean

  def toJbj(value: Boolean)(implicit ctx: Context) = BooleanVal(value)
}
