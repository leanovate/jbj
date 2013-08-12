package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, DoubleVal}
import de.leanovate.jbj.runtime.Context

object DoubleConverter extends Converter[Double, DoubleVal] {
  override def toScalaWithConversion(valueOrRef: ValueOrRef)(implicit ctx: Context) = toScala(valueOrRef.value.toDouble)

  override def toScala(value: DoubleVal)(implicit ctx: Context) = value.asDouble

  override def toJbj(value: Double)(implicit ctx: Context) = DoubleVal(value)
}
