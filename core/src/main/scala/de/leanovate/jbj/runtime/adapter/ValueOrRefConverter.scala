package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}
import de.leanovate.jbj.runtime.Context

object ValueOrRefConverter extends Converter[ValueOrRef, Value] {
  override def toScalaWithConversion(valueOrRef: ValueOrRef)(implicit ctx: Context) = valueOrRef

  override def toScala(value: Value)(implicit ctx: Context) = value

  override def toJbj(valueOrRef: ValueOrRef)(implicit ctx: Context) = valueOrRef.value
}
