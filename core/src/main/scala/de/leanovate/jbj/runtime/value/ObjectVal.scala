package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime._
import scala.collection.mutable
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context

class ObjectVal(var pClass: PClass, var instanceNum: Long, private val keyValueMap: mutable.LinkedHashMap[Any, PAny])
  extends PVal with ArrayLike {

  private val visibilities = mutable.Map.empty[Any, PVisibility.Type]

  def keyValues(implicit ctx: Context): Seq[(PVal, (PVisibility.Type, PAny))] = keyValueMap.toSeq.map {
    case (key: Long, value) => IntegerVal(key) -> (visibilities.get(key).getOrElse(PVisibility.PUBLIC), value)
    case (key: String, value) => StringVal(key) -> (visibilities.get(key).getOrElse(PVisibility.PUBLIC), value)
  }

  override def toOutput(implicit ctx: Context) = "Array"

  override def toStr(implicit ctx: Context) = StringVal("object".getBytes(ctx.settings.charset))

  override def toNum(implicit ctx: Context) = toInteger

  override def toDouble(implicit ctx: Context) = DoubleVal(0.0)

  override def toInteger(implicit ctx: Context) = IntegerVal(0)

  override def toBool(implicit ctx: Context) = BooleanVal.FALSE

  override def toArray(implicit ctx: Context) = new ArrayVal(keyValueMap.clone())

  override def isNull = false

  override def copy(implicit ctx: Context) = new ObjectVal(pClass, ctx.global.instanceCounter.incrementAndGet(), keyValueMap.clone())

  override def incr = this

  override def decr = this

  override def typeName = "object"

  override def compare(other: PVal)(implicit ctx: Context): Int = other match {
    case otherObj: ObjectVal =>
      if (pClass != otherObj.pClass)
        return Int.MinValue
      val keyIt = keyValueMap.keysIterator
      while (keyIt.hasNext) {
        val key = keyIt.next()
        val thisVal = keyValueMap(key)
        val otherVal = otherObj.keyValueMap.get(key)

        if (otherVal.isEmpty) {
          if (keyIt.hasNext)
            return Int.MinValue
          else
            return -1
        } else {
          val comp = thisVal.asVal.compare(otherVal.get.asVal)

          if (comp != null)
            return comp
        }
      }
      0
    case _ => 1
  }

  final def instanceOf(other: PClass): Boolean = other.isAssignableFrom(pClass)

  def getProperty(name: String)(implicit ctx: Context): Option[PAny] = keyValueMap.get(name)

  def setProperty(name: String, visibility:Option[PVisibility.Type], value: PAny) {
    visibility.foreach(visibilities.put(name, _))
    keyValueMap.get(name).foreach(_.cleanup())
    keyValueMap.put(name, value)
  }

  def unsetProperty(name: String) = {
    keyValueMap.remove(name).foreach(_.cleanup())
  }

  override def size: Int = keyValueMap.size

  override def getAt(index: Long)(implicit ctx: Context): Option[PAny] =
    throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))

  override def getAt(index: String)(implicit ctx: Context): Option[PAny] =
    throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))

  override def setAt(index: Long, value: PAny)(implicit ctx: Context) {
    throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))
  }

  override def setAt(index: String, value: PAny)(implicit ctx: Context) {
    throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))
  }

  override def append(value: PAny)(implicit ctx: Context) {
    throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))
  }

  override def unsetAt(index: Long)(implicit ctx: Context) {
    throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))
  }

  override def unsetAt(index: String)(implicit ctx: Context) {
    throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))
  }
}

object ObjectVal {
  def apply(pClass: PClass, keyValues: (Option[PVal], PVal)*)(implicit ctx: Context): ObjectVal = {
    var nextIndex: Long = -1

    new ObjectVal(pClass, ctx.global.instanceCounter.incrementAndGet,
      keyValues.foldLeft(mutable.LinkedHashMap.newBuilder[Any, PAny]) {
        (builder, keyValue) =>
          val key = keyValue._1.map {
            case IntegerVal(value) =>
              if (value > nextIndex)
                nextIndex = value
              value
            case NumericVal(value) =>
              if (value > nextIndex)
                nextIndex = value.toLong
              value.toLong
            case value =>
              value.toStr.asString
          }.getOrElse {
            nextIndex += 1
            nextIndex
          }

          builder += (key -> keyValue._2)
      }.result())
  }

  def unapply(obj: ObjectVal)(implicit ctx: Context) = Some(obj.pClass, obj.instanceNum, obj.keyValues)
}