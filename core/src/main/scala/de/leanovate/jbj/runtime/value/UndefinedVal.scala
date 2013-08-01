package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import de.leanovate.jbj.runtime.{ArrayKey, Value}

object UndefinedVal extends Value {
  override def toOutput(out: PrintStream) {
  }

  override def toDump(out: PrintStream, ident: String = "") {
    out.println("%sNULL".format(ident))
  }

  override def toStr = StringVal("")

  override def toNum = toInteger

  override def toInteger = IntegerVal(0)

  override def toBool = BooleanVal(false)

  override def isNull = false

  override def isUndefined = true

  override def copy = this

  override def incr = IntegerVal(1)

  override def decr = NullVal

  override def getAt(index: ArrayKey) = this

  override def setAt(index: ArrayKey, value: Value) {}
}
