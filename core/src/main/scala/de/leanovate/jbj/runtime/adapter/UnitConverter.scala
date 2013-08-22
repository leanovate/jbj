package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{NullVal, PVal}
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

object UnitConverter extends Converter[Unit, PVal] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) {}

  def toScala(value: PVal)(implicit ctx: Context) {}

  def toJbj(value: Unit)(implicit ctx: Context) = NullVal
}
