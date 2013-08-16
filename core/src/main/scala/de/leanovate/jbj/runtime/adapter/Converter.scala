package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, PAnyVal}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.Expr

trait Converter[ScalaType, JbjType <: PAnyVal] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context): ScalaType

  def toScala(value: JbjType)(implicit ctx: Context): ScalaType

  def toJbj(value: ScalaType)(implicit ctx: Context): JbjType
}
