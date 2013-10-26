package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, ObjectVal}
import de.leanovate.jbj.runtime.types.{PStdClass, PAnyParam, PInterfaceAdapter, PParam}
import de.leanovate.jbj.runtime.context.Context

case class PInterfaceConverter[T <: ObjectVal](adapter: PInterfaceAdapter[T]) extends Converter[T, ObjectVal] {
  override def typeName = adapter.name.toString

  override def missingValue(implicit ctx: Context) = adapter.cast(PStdClass.newInstance(Nil))

  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = {
    pAny.asVal.concrete match {
      case obj: ObjectVal =>
        adapter.cast(obj)
      case _ =>
        missingValue
    }
  }

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = {
    param.byVal.concrete match {
      case obj: ObjectVal =>
        adapter.cast(obj)
      case _ =>
        missingValue
    }
  }

  override def toScala(value: PAny)(implicit ctx: Context) = value.asVal.concrete match {
    case obj: ObjectVal => Some(adapter.cast(obj))
    case _ => None
  }

  override def toJbj(value: T)(implicit ctx: Context) = value
}
