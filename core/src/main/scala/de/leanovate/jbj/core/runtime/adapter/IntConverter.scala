package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.IntegerVal
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

object IntConverter extends Converter[Int, IntegerVal] {

  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = toScala(expr.eval.asVal.toInteger)

  def toScala(value: IntegerVal)(implicit ctx: Context) = value.asInt

  def toJbj(value: Int)(implicit ctx: Context) = IntegerVal(value)
}
