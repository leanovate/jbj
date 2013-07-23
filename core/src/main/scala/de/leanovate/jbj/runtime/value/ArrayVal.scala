package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import de.leanovate.jbj.exception.FatalErrorException
import de.leanovate.jbj.runtime.Value

case class ArrayVal(keyValues: List[(Value, Value)]) extends Value {
  def toOutput(out: PrintStream) {
    out.print("Array")
  }

  def toDump(out: PrintStream, ident: String = "") {
    val nextIdent = ident + "  "
    out.println("%sarray(%d) {".format(ident, keyValues.length))
    keyValues.foreach {
      case (IntegerVal(key), value) =>
        out.println("%s[%d]=>".format(nextIdent, key))
        value.toDump(out, ident + "  ")
      case (key, value) =>
        out.println( """%s["%s"]=>""".format(nextIdent, key.toStr.value))
        value.toDump(out, ident + "  ")
    }
    out.println("%s}".format(ident))
  }

  def toStr = StringVal("Array")

  def toNum = {
    throw new FatalErrorException("Invalid conversion")
  }

  def toBool = {
    throw new FatalErrorException("Invalid conversion")
  }

  def isNull = false

  def isUndefined = false

  def isTrue = false

  def unref = this

  def copy = ArrayVal(List(keyValues: _*))

  def incr = this

  def decr = this
}
