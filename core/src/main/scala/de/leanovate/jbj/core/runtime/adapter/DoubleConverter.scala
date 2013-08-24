package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.DoubleVal
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

object DoubleConverter extends Converter[Double, DoubleVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = toScala(expr.eval.asVal.toDouble)

  override def toScala(value: DoubleVal)(implicit ctx: Context) = value.asDouble

  override def toJbj(value: Double)(implicit ctx: Context) = DoubleVal(value)
}
