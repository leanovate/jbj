package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, IntegerVal}
import de.leanovate.jbj.runtime.Context

object LongConverter extends Converter[Long, IntegerVal] {
  override def toScalaWithConversion(valueOrRef: ValueOrRef)(implicit ctx: Context) = toScala(valueOrRef.value.toInteger)

  override def toScala(value: IntegerVal)(implicit ctx: Context) = value.asLong

  override def toJbj(value: Long)(implicit ctx: Context) = IntegerVal(value)
}
