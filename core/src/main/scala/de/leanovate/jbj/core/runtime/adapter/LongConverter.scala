package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.IntegerVal
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

object LongConverter extends Converter[Long, IntegerVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = toScala(expr.eval.asVal.toInteger)

  override def toScala(value: IntegerVal)(implicit ctx: Context) = value.asLong

  override def toJbj(value: Long)(implicit ctx: Context) = IntegerVal(value)
}
