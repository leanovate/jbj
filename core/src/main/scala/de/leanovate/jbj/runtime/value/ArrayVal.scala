package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime._
import scala.collection.mutable
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.runtime.StringArrayKey
import de.leanovate.jbj.ast.NodePosition

class ArrayVal(var keyValues: mutable.LinkedHashMap[ArrayKey, ValueOrRef]) extends Value with ArrayLike {

  private var maxIndex: Long = (0L :: keyValues.keys.map {
    case IntArrayKey(idx) => idx
    case _ => 0L
  }.toList).max

  override def toOutput(implicit ctx: Context) = "Array"

  override def toStr(implicit ctx: Context) = StringVal("Array".getBytes(ctx.settings.charset))

  override def toNum(implicit ctx: Context) = toInteger

  override def toDouble(implicit ctx: Context) = DoubleVal(0.0)

  override def toInteger(implicit ctx: Context) = IntegerVal(keyValues.size)

  override def toBool(implicit ctx: Context) = BooleanVal.FALSE

  override def toArray(implicit ctx: Context) = this

  override def isNull = false

  def isEmpty = keyValues.isEmpty

  override def copy = new ArrayVal(keyValues.clone())

  override def incr = this

  override def decr = this

  override def getAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition) = keyValues.get(index)

  override def setAt(index: Option[ArrayKey], value: ValueOrRef)(implicit ctx: Context, position: NodePosition) {
    index match {
      case Some(key@IntArrayKey(idx)) if idx > maxIndex =>
        keyValues.get(key).foreach(_.decrRefCount())
        keyValues.put(key, value)
        value.incrRefCount()
        maxIndex = idx
      case Some(key) =>
        keyValues.get(key).foreach(_.decrRefCount())
        keyValues.put(key, value)
        value.incrRefCount()
      case None =>
        keyValues.get(IntArrayKey(maxIndex)).foreach(_.decrRefCount())
        keyValues.put(IntArrayKey(maxIndex), value)
        value.incrRefCount()
        maxIndex += 1
    }
  }

  override def unsetAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition) {
    keyValues.remove(index).foreach(_.decrRefCount())
  }


  def count: IntegerVal = IntegerVal(keyValues.size)
}

object ArrayVal {
  def apply(keyValues: (Option[Value], Value)*)(implicit ctx: Context): ArrayVal = {
    var nextIndex: Long = -1

    new ArrayVal(keyValues.foldLeft(mutable.LinkedHashMap.newBuilder[ArrayKey, ValueOrRef]) {
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

  def unapply(array: ArrayVal) = Some(array.keyValues)
}