package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import de.leanovate.jbj.runtime._
import scala.collection.mutable
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.runtime.StringArrayKey
import de.leanovate.jbj.ast.NodePosition

class ArrayVal(var keyValues: mutable.LinkedHashMap[ArrayKey, Value]) extends Value {

  private var maxIndex: Long = (0L :: keyValues.keys.map {
    case IntArrayKey(idx) => idx
    case _ => 0L
  }.toList).max

  override def toOutput(out: PrintStream) {
    out.print("Array")
  }

  override def toDump(out: PrintStream, ident: String = "") {
    val nextIdent = ident + "  "
    out.println("%sarray(%d) {".format(ident, keyValues.size))
    keyValues.foreach {
      case (IntArrayKey(key), value) =>
        out.println("%s[%d]=>".format(nextIdent, key))
        value.toDump(out, ident + "  ")
      case (StringArrayKey(key), value) =>
        out.println( """%s["%s"]=>""".format(nextIdent, key))
        value.toDump(out, ident + "  ")
    }
    out.println("%s}".format(ident))
  }

  override def toStr = StringVal("Array")

  override def toNum = toInteger

  override def toInteger = IntegerVal(0)

  override def toBool = BooleanVal.FALSE

  override def isNull = false

  override def isUndefined = false

  def isEmpty = keyValues.isEmpty

  override def copy = new ArrayVal(keyValues.clone())

  override def incr = this

  override def decr = this

  override def getAt(index: ArrayKey) = keyValues.getOrElse(index, UndefinedVal)

  override def setAt(index: Option[ArrayKey], value: Value)(implicit ctx: Context, position: NodePosition) {
    index match {
      case Some(key@IntArrayKey(idx)) if idx > maxIndex =>
        keyValues.put(key, value)
        maxIndex = idx
      case Some(key) =>
        keyValues.put(key, value)
      case None =>
        maxIndex += 1
        keyValues.put(IntArrayKey(maxIndex), value)
    }
  }

  def count: IntegerVal = IntegerVal(keyValues.size)
}

object ArrayVal {
  def apply(keyValues: (Option[Value], Value)*): ArrayVal = {
    var nextIndex: Long = -1

    new ArrayVal(keyValues.foldLeft(mutable.LinkedHashMap.newBuilder[ArrayKey, Value]) {
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
            StringArrayKey(value.toStr.value)
        }.getOrElse {
          nextIndex += 1
          IntArrayKey(nextIndex)
        }

        builder += key -> keyValue._2
    }.result())
  }
}