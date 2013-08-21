package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime._
import scala.collection.mutable
import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.context.Context

class ArrayVal(private val keyValueMap: mutable.LinkedHashMap[Any, PAny]) extends PVal with ArrayLike {

  private var maxIndex: Long = (0L :: keyValueMap.keys.map {
    case idx: Long => idx
    case _ => 0L
  }.toList).max

  def keyValues(implicit ctx: Context): Seq[(PVal, PAny)] = keyValueMap.toSeq.map {
    case (key: Long, value) => IntegerVal(key) -> value
    case (key: String, value) => StringVal(key) -> value
  }

  override def toOutput(implicit ctx: Context) = "Array"

  override def toStr(implicit ctx: Context) = StringVal("Array".getBytes(ctx.settings.charset))

  override def toNum(implicit ctx: Context) = toInteger

  override def toDouble(implicit ctx: Context) = DoubleVal(0.0)

  override def toInteger(implicit ctx: Context) = IntegerVal(keyValueMap.size)

  override def toBool(implicit ctx: Context) = BooleanVal.FALSE

  override def toArray(implicit ctx: Context) = this

  override def isNull = false

  def isEmpty = keyValueMap.isEmpty

  override def copy(implicit ctx: Context) = new ArrayVal(keyValueMap.clone())

  override def incr = this

  override def decr = this

  override def typeName: String = "array"

  override def size: Int = keyValueMap.size

  override def compare(other: PVal)(implicit ctx: Context): Int = other match {
    case otherArray: ArrayVal =>
      val keyIt = keyValueMap.keysIterator
      while (keyIt.hasNext) {
        val key = keyIt.next()
        val thisVal = keyValueMap(key)
        val otherVal = otherArray.keyValueMap.get(key)

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

  override def getAt(index: Long)(implicit ctx: Context): Option[PAny] =
    keyValueMap.get(index)

  override def getAt(index: String)(implicit ctx: Context): Option[PAny] =
    keyValueMap.get(index)

  override def setAt(index: Long, value: PAny)(implicit ctx: Context) {
    if (index > maxIndex) {
      maxIndex = index
    }
    keyValueMap.get(index).foreach(_.cleanup())
    keyValueMap.put(index, value)
  }

  override def setAt(index: String, value: PAny)(implicit ctx: Context) {
    keyValueMap.get(index).foreach(_.cleanup())
    keyValueMap.put(index, value)
  }

  override def append(value: PAny)(implicit ctx: Context) {
    keyValueMap.get(maxIndex).foreach(_.cleanup())
    keyValueMap.put(maxIndex, value)
    maxIndex += 1
  }

  override def unsetAt(index: Long)(implicit ctx: Context) {
    keyValueMap.remove(index).foreach(_.cleanup())
  }

  override def unsetAt(index: String)(implicit ctx: Context) {
    keyValueMap.remove(index).foreach(_.cleanup())
  }

  def count: IntegerVal = IntegerVal(keyValueMap.size)
}

object ArrayVal {
  def apply(): ArrayVal = new ArrayVal(mutable.LinkedHashMap.empty[Any, PAny])

  def apply(keyValues: (Option[PVal], PAny)*)(implicit ctx: Context): ArrayVal = {
    var nextIndex: Long = -1

    new ArrayVal(keyValues.foldLeft(mutable.LinkedHashMap.newBuilder[Any, PAny]) {
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

        builder += key -> keyValue._2
    }.result())
  }

  def unapply(array: ArrayVal)(implicit ctx: Context) = Some(array.keyValues)
}