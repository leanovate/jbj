package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, IntegerVal}
import de.leanovate.jbj.runtime.Context

object IntConverter extends Converter[Int, IntegerVal] {

  def toScalaWithConversion(valueOrRef: ValueOrRef)(implicit ctx: Context) = toScala(valueOrRef.value.toInteger)

  def toScala(value: IntegerVal)(implicit ctx: Context) = value.asInt

  def toJbj(value: Int)(implicit ctx: Context) = IntegerVal(value)
}
