package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, StringVal}
import de.leanovate.jbj.runtime.Context

object ByteArrayConverter extends Converter[Array[Byte], StringVal] {
  def toScalaWithConversion(valueOrRef: ValueOrRef)(implicit ctx: Context) = valueOrRef.value.toStr.chars

  def toScala(value: StringVal)(implicit ctx: Context) = value.chars

  def toJbj(value: Array[Byte])(implicit ctx: Context) = StringVal(value)
}
