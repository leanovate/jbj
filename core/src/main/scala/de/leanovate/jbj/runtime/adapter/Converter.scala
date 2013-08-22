package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

trait Converter[ScalaType, JbjType <: PAny] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context): ScalaType

  def toScala(value: JbjType)(implicit ctx: Context): ScalaType

  def toJbj(value: ScalaType)(implicit ctx: Context): JbjType
}
