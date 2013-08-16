package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{NullVal, PAny, PAnyVal}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.Expr

object UnitConverter extends Converter[Unit, PAnyVal] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) {}

  def toScala(value: PAnyVal)(implicit ctx: Context) {}

  def toJbj(value: Unit)(implicit ctx: Context) = NullVal
}
