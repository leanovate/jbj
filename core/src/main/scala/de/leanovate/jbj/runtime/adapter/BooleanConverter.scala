package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, BooleanVal}
import de.leanovate.jbj.runtime.Context

object BooleanConverter extends Converter[Boolean, BooleanVal] {
  override def toScalaWithConversion(valueOrRef: ValueOrRef)(implicit ctx: Context) = toScala(valueOrRef.value.toBool)

  override def toScala(value: BooleanVal)(implicit ctx: Context) = value.asBoolean

  def toJbj(value: Boolean)(implicit ctx: Context) = BooleanVal(value)
}
