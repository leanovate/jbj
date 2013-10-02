package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, ObjectVal}
import de.leanovate.jbj.runtime.types.{PAnyParam, PInterfaceAdapter, PParam}
import de.leanovate.jbj.runtime.context.Context

case class PInterfaceConverter[T <: ObjectVal](adapter: PInterfaceAdapter[T]) extends Converter[T, ObjectVal] {
  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = {
    pAny.asVal.concrete match {
      case obj: ObjectVal =>
        toScala(obj)
    }
  }

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = {
    param.byVal.concrete match {
      case obj: ObjectVal =>
        toScala(obj)
    }
  }

  override def toScala(value: ObjectVal)(implicit ctx: Context) = {
    adapter.cast(value)
  }

  override def toJbj(value: T)(implicit ctx: Context) = value
}
