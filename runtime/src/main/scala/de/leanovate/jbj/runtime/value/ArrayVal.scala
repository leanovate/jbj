/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import scala.collection.mutable
import de.leanovate.jbj.runtime.context.Context

class ArrayVal(private val keyValueMap: mutable.LinkedHashMap[Any, PAny]) extends PConcreteVal with ArrayLike {

  private var maxIndex: Long = (-1L :: keyValueMap.keys.map {
    case idx: Long => idx
    case _ => -1L
  }.toList).max

  private var iteratorState: Option[BufferedIterator[(Any, PAny)]] = None

  def keyValues(implicit ctx: Context): Seq[(PVal, PAny)] = keyValueMap.toSeq.map {
    case (key: Long, value) => IntegerVal(key) -> value
    case (key: String, value) => StringVal(key) -> value
  }

  override def toOutput(implicit ctx: Context) = "Array"

  override def toStr(implicit ctx: Context) = StringVal("Array".getBytes("UTF-8"))

  override def toNum = toInteger

  override def toDouble = DoubleVal(0.0)

  override def toInteger = IntegerVal(keyValueMap.size)

  override def toBool = BooleanVal(!keyValueMap.isEmpty)

  override def toArray(implicit ctx: Context) = this

  override def isNull = false

  def isEmpty = keyValueMap.isEmpty

  override def copy = {
    iteratorState = None
    new ArrayVal(keyValueMap.map {
      case (key, pVar: PVar) =>
        pVar.retain()
        key -> pVar
      case (key, pVal: PVal) => key -> pVal.copy
    })
  }

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

  override def setAt(index: Long, value: PAny)(implicit ctx: Context) = {
    if (index > maxIndex) {
      maxIndex = index
    }
    value.retain()
    keyValueMap.get(index).foreach(_.release())
    keyValueMap.put(index, value)
    value
  }

  override def setAt(index: String, value: PAny)(implicit ctx: Context) = {
    value.retain()
    keyValueMap.get(index).foreach(_.release())
    keyValueMap.put(index, value)
    value
  }

  override def append(value: PAny)(implicit ctx: Context) = {
    maxIndex += 1
    value.retain()
    keyValueMap.get(maxIndex).foreach(_.release())
    keyValueMap.put(maxIndex, value)
    value
  }

  override def unsetAt(index: Long)(implicit ctx: Context) {
    keyValueMap.remove(index).foreach(_.release())
  }

  override def unsetAt(index: String)(implicit ctx: Context) {
    keyValueMap.remove(index).foreach(_.release())
  }

  def count: IntegerVal = IntegerVal(keyValueMap.size)

  def iteratorHasNext: Boolean = {
    if (iteratorState.isEmpty) {
      val it = keyValueMap.iterator
      iteratorState = Some(it.buffered)
    }
    iteratorState.get.hasNext
  }

  def iteratorCurrent(implicit ctx: Context): PVal =
    if (!iteratorHasNext) {
      BooleanVal.FALSE
    } else {
      iteratorState.get.head match {
        case (_, value) => value.asVal
      }
    }

  def iteratorNext()(implicit ctx: Context): PVal =
    if (!iteratorHasNext) {
      BooleanVal.FALSE
    } else {
      iteratorState.get.next() match {
        case (key: Long, value) =>
          ArrayVal(Some(IntegerVal(1)) -> value, Some(StringVal("value")) -> value,
            Some(IntegerVal(0)) -> IntegerVal(key), Some(StringVal("key")) -> IntegerVal(key))
        case (key: String, value) =>
          ArrayVal(Some(IntegerVal(1)) -> value, Some(StringVal("value")) -> value,
            Some(IntegerVal(0)) -> StringVal(key), Some(StringVal("key")) -> StringVal(key))
      }
    }

  def iteratorAdvance() {
    if (iteratorHasNext) {
      iteratorState.get.next()
    }
  }

  def iteratorReset() {
    iteratorState = None
  }

  def cleanup()(implicit ctx: Context) {
    keyValueMap.values.foreach(_.release())
  }
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
          case DoubleVal(value) =>
            val intVal = value.toLong
            if (intVal > nextIndex)
              nextIndex = intVal
            intVal
          case str: StringVal if str.isStrongNumericPattern =>
            val intVal = str.toInteger.asLong
            if (intVal > nextIndex)
              nextIndex = intVal
            intVal
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