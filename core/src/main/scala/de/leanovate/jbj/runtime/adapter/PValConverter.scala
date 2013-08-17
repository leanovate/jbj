package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, PVal}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.Expr

object PValConverter extends Converter[PVal, PVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = expr.eval

  override def toScala(value: PVal)(implicit ctx: Context) = value

  override def toJbj(value: PVal)(implicit ctx: Context) = value
}
