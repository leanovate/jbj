package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, PAnyVal}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.Expr

object ValueConverter extends Converter[PAnyVal, PAnyVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = expr.eval

  override def toScala(value: PAnyVal)(implicit ctx: Context) = value

  override def toJbj(value: PAnyVal)(implicit ctx: Context) = value
}
