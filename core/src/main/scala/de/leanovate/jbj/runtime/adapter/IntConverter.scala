package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, IntegerVal}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.Expr

object IntConverter extends Converter[Int, IntegerVal] {

  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = toScala(expr.eval.asVal.toInteger)

  def toScala(value: IntegerVal)(implicit ctx: Context) = value.asInt

  def toJbj(value: Int)(implicit ctx: Context) = IntegerVal(value)
}
