package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Value
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.ArrayVal.{ArrayKey, StringArrayKey, IntArrayKey}
import de.leanovate.jbj.exception.FatalErrorException

class ObjectVal(val keyValues: List[(ArrayKey, Value)]) extends Value {
  private lazy val keyValueMap = keyValues.toMap

  def toOutput(out: PrintStream) {
    out.print("Array")
  }

  def toDump(out: PrintStream, ident: String = "") {
    val nextIdent = ident + "  "
    out.println("%sarray(%d) {".format(ident, keyValues.length))
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

  def toStr = StringVal("object")

  def toNum = {
    throw new FatalErrorException("Invalid conversion")
  }

  def toBool = {
    throw new FatalErrorException("Invalid conversion")
  }

  def isNull = false

  def isUndefined = false

  def unref = this

  def copy = this

  def incr = this

  def decr = this

  def getAt(index: Value) = index.unref match {
    case IntegerVal(idx) => keyValueMap.getOrElse(IntArrayKey(idx.toInt), UndefinedVal)
    case NumericVal(idx) => keyValueMap.getOrElse(IntArrayKey(idx.toInt), UndefinedVal)
    case StringVal(idx) => keyValueMap.getOrElse(StringArrayKey(idx), UndefinedVal)
    case _ => UndefinedVal
  }
}

object ObjectVal {
  def apply(keyValues: List[(Option[Value], Value)]): ObjectVal = {
    var nextIndex: Long = -1

    new ObjectVal(keyValues.map {
      keyValue =>
        val key = keyValue._1.map {
          k => k.unref match {
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
          }
        }.getOrElse {
          nextIndex += 1
          IntArrayKey(nextIndex)
        }

        (key, keyValue._2)
    })
  }

}