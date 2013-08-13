package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}
import de.leanovate.jbj.runtime.Context

object DirectConverter extends Converter[Value, Value] {
  override def toScalaWithConversion(valueOrRef: ValueOrRef)(implicit ctx: Context) = valueOrRef.value

  override def toScala(value: Value)(implicit ctx: Context) = value

  override def toJbj(value: Value)(implicit ctx: Context) = value
}
