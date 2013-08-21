package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, PVal}
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

object PValConverter extends Converter[PVal, PVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = expr.evalOld

  override def toScala(value: PVal)(implicit ctx: Context) = value

  override def toJbj(value: PVal)(implicit ctx: Context) = value
}
