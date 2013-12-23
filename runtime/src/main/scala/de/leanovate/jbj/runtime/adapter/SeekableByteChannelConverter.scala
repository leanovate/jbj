package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ResourceVal, PAny, StreamResourceVal}
import java.nio.channels.SeekableByteChannel
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam

object SeekableByteChannelConverter extends Converter[SeekableByteChannel, StreamResourceVal] {
  def typeName = "resource"

  def missingValue(implicit ctx: Context) = null

  def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = pAny.asVal.concrete match {
    case ResourceVal(_, stream: SeekableByteChannel) => stream
    case _ =>
      missingValue
  }

  def toScalaWithConversion(param: PParam)(implicit ctx: Context) = toScalaWithConversion(param.byVal)

  def toScala(value: PAny)(implicit ctx: Context) = value.asVal.concrete match {
    case ResourceVal(_, stream: SeekableByteChannel) => Some(stream)
    case _ => None
  }

  def toJbj(stream: SeekableByteChannel)(implicit ctx: Context) = StreamResourceVal(stream)
}
