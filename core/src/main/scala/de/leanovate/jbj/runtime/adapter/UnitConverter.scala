package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{NullVal, ValueOrRef, Value}
import de.leanovate.jbj.runtime.Context

object UnitConverter extends Converter[Unit, Value] {
  def toScalaWithConversion(valueOrRef: ValueOrRef)(implicit ctx: Context) {}

  def toScala(value: Value)(implicit ctx: Context) {}

  def toJbj(value: Unit)(implicit ctx: Context) = NullVal
}
