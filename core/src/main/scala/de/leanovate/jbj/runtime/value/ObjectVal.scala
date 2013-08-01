package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.{PClass, Value}
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.ArrayVal.{ArrayKey, StringArrayKey, IntArrayKey}
import de.leanovate.jbj.exception.FatalErrorException
import scala.collection.mutable

class ObjectVal(pClass: PClass, _keyValues: mutable.Seq[(ArrayKey, Value)]) extends Value {
  private lazy val keyValueMap = _keyValues.toMap

  def keyValues: List[(ArrayKey, Value)] = _keyValues.toList

  def toOutput(out: PrintStream) {
    out.print("Array")
  }

  def toDump(out: PrintStream, ident: String = "") {
    val nextIdent = ident + "  "
    out.println("%sarray(%d) {".format(ident, _keyValues.length))
    _keyValues.foreach {
      case (IntArrayKey(key), value) =>
        out.println("%s[%d]=>".format(nextIdent, key))
        value.toDump(out, ident + "  ")
      case (StringArrayKey(key), value) =>
        out.println( """%s["%s"]=>""".format(nextIdent, key))
        value.toDump(out, ident + "  ")
    }
    out.println("%s}".format(ident))
  }

  def toStr = StringVal("object")

  def toNum = toInteger

  def toInteger = IntegerVal(0)

  def toBool = {
    throw new FatalErrorException("Invalid conversion")
  }

  def isNull = false

  def isUndefined = false

  def copy = ObjectVal(pClass, _keyValues)

  def incr = this

  def decr = this

  def getAt(index: Value) = index match {
    case IntegerVal(idx) => keyValueMap.getOrElse(IntArrayKey(idx.toInt), UndefinedVal)
    case NumericVal(idx) => keyValueMap.getOrElse(IntArrayKey(idx.toInt), UndefinedVal)
    case StringVal(idx) => keyValueMap.getOrElse(StringArrayKey(idx), UndefinedVal)
    case _ => UndefinedVal
  }
}

object ObjectVal {
  def apply(pClass: PClass, keyValues: List[(Option[Value], Value)]): ObjectVal = {
    var nextIndex: Long = -1

    new ObjectVal(pClass, keyValues.foldLeft(mutable.Seq.newBuilder[(ArrayKey, Value)]) {
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

        builder += (key -> keyValue._2)
    }.result())
  }

  def apply(pClass: PClass, keyValues: Seq[(ArrayKey, Value)]): ObjectVal = {
    val copyKeyValues = mutable.Seq.newBuilder[(ArrayKey, Value)]
    copyKeyValues ++= keyValues
    new ObjectVal(pClass, copyKeyValues.result())
  }
}