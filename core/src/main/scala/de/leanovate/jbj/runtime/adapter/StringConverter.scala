package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, StringVal}
import de.leanovate.jbj.runtime.Context

object StringConverter extends Converter[String, StringVal] {
  override def toScalaWithConversion(valueOrRef: ValueOrRef)(implicit ctx: Context) = toScala(valueOrRef.value.toStr)

  override def toScala(value: StringVal)(implicit ctx: Context) = value.asString

  override def toJbj(value: String)(implicit ctx: Context) = StringVal(value)
}
