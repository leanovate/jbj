package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

abstract class ResourceVal[T](var id: Long, var resourceType: String, var payload: T) extends PConcreteVal {

  override def toOutput(implicit ctx: Context) = s"resource id #$id"

  def toStr(implicit ctx: Context) = StringVal(s"resource id #$id")

  def toNum = IntegerVal(0)

  def toInteger = IntegerVal(0)

  def toDouble = DoubleVal(0)

  def toBool = BooleanVal.FALSE

  def toArray(implicit ctx: Context) = ArrayVal(None -> this)

  def isScalar = true

  def isNull = false

  def copy = this

  def incr = this

  def decr = this

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