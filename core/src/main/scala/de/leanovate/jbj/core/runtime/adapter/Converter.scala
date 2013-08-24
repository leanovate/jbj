package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.PAny
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

trait Converter[ScalaType, JbjType <: PAny] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context): ScalaType

  def toScala(value: JbjType)(implicit ctx: Context): ScalaType

  def toJbj(value: ScalaType)(implicit ctx: Context): JbjType
}
