package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.DoubleVal
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

object DoubleConverter extends Converter[Double, DoubleVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = toScala(expr.eval.asVal.toDouble)

  override def toScala(value: DoubleVal)(implicit ctx: Context) = value.asDouble

  override def toJbj(value: Double)(implicit ctx: Context) = DoubleVal(value)
}
