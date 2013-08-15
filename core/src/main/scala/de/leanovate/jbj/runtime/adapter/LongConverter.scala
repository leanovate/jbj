package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, IntegerVal}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.Expr

object LongConverter extends Converter[Long, IntegerVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = toScala(expr.eval.toInteger)

  override def toScala(value: IntegerVal)(implicit ctx: Context) = value.asLong

  override def toJbj(value: Long)(implicit ctx: Context) = IntegerVal(value)
}
