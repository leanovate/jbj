package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime._
import scala.collection.mutable
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.runtime.StringArrayKey
import de.leanovate.jbj.ast.NodePosition

class ArrayVal(keyValueMap: mutable.LinkedHashMap[ArrayKey, PAny]) extends PVal with ArrayLike {

  private var maxIndex: Long = (0L :: keyValueMap.keys.map {
    case IntArrayKey(idx) => idx
    case _ => 0L
  }.toList).max

  def keyValues(implicit ctx: Context): Seq[(PVal, PAny)] = keyValueMap.toSeq.map {
    case (key, value) => key.value -> value
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

  override def copy = new ArrayVal(keyValueMap.clone())

  override def incr = this

  override def decr = this

  override def size: Int = keyValueMap.size

  override def getAt(index: Long)(implicit ctx: Context, position: NodePosition): Option[PAny] =
    keyValueMap.get(IntArrayKey(index))

  override def getAt(index: String)(implicit ctx: Context, position: NodePosition): Option[PAny] =
    keyValueMap.get(StringArrayKey(index))

  override def setAt(index: Long, value: PAny)(implicit ctx: Context, position: NodePosition) {
    if (index > maxIndex) {
      maxIndex = index
    }
    val key = IntArrayKey(index)
    keyValueMap.get(key).foreach(_.decrRefCount())
    keyValueMap.put(key, value)
    value.incrRefCount()
  }

  override def setAt(index: String, value: PAny)(implicit ctx: Context, position: NodePosition) {
    val key = StringArrayKey(index)
    keyValueMap.get(key).foreach(_.decrRefCount())
    keyValueMap.put(key, value)
    value.incrRefCount()
  }

  override def append(value: PAny)(implicit ctx: Context, position: NodePosition) {
    keyValueMap.get(IntArrayKey(maxIndex)).foreach(_.decrRefCount())
    keyValueMap.put(IntArrayKey(maxIndex), value)
    value.incrRefCount()
    maxIndex += 1
  }

  override def unsetAt(index: Long)(implicit ctx: Context, position: NodePosition) {
    keyValueMap.remove(IntArrayKey(index)).foreach(_.decrRefCount())
  }

  override def unsetAt(index: String)(implicit ctx: Context, position: NodePosition) {
    keyValueMap.remove(StringArrayKey(index)).foreach(_.decrRefCount())
  }

  def count: IntegerVal = IntegerVal(keyValueMap.size)
}

object ArrayVal {
  def apply(keyValues: (Option[PVal], PAny)*)(implicit ctx: Context): ArrayVal = {
    var nextIndex: Long = -1

    new ArrayVal(keyValues.foldLeft(mutable.LinkedHashMap.newBuilder[ArrayKey, PAny]) {
      (builder, keyValue) =>
        val key = keyValue._1.map {
          case IntegerVal(value) =>
            if (value > nextIndex)
              nextIndex = value
            IntArrayKey(value)
          case NumericVal(value) =>
            if (value > nextIndex)
              nextIndex = value.toInt
            IntArrayKey(value.toInt)
          case value =>
            StringArrayKey(value.toStr.asString)
        }.getOrElse {
          nextIndex += 1
          IntArrayKey(nextIndex)
        }

        keyValue._2.incrRefCount()
        builder += key -> keyValue._2
    }.result())
  }

  def unapply(array: ArrayVal)(implicit ctx: Context) = Some(array.keyValues)
}