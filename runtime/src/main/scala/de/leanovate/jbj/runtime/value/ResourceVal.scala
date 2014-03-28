package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

abstract class ResourceVal[T](var id: Long, var resourceType: String, var payload: T) extends PConcreteVal {

  override def toOutput(implicit ctx: Context) = s"resource id #$id"

  override def toStr(implicit ctx: Context) = StringVal(s"resource id #$id")

  override def toNum(implicit ctx: Context) = IntegerVal(0)

  override def toInteger(implicit ctx: Context) = IntegerVal(0)

  override def toDouble = DoubleVal(0)

  override def toBool = BooleanVal.FALSE

  override def toArray(implicit ctx: Context) = ArrayVal(None -> this)

  override def isScalar = true

  override def isNull = false

  override def copy = this

  override def incr(implicit ctx: Context) = this

  override def decr(implicit ctx: Context) = this

  def typeName(simple: Boolean) = "resource"

  def compare(other: PVal)(implicit ctx: Context) = {
    StringVal.compare(toStr.chars, other.toStr.chars)
  }

  override def isCallable(implicit ctx: Context) = false

  override def call(params: List[PParam])(implicit ctx: Context) =
    throw new FatalErrorJbjException("Function name must be a string")

  def isOpen: Boolean

  def close()
}

object ResourceVal {
  def unapply[T](resource: ResourceVal[T]) = Some(resource.resourceType, resource.payload)
}