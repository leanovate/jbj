package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import de.leanovate.jbj.runtime.Value

object UndefinedVal extends Value {
  def toOutput(out: PrintStream) {
  }

  def toDump(out: PrintStream, ident: String = "") {
    out.println("%sNULL".format(ident))
  }

  def toStr = StringVal("")

  def toNum = IntegerVal(0)

  def toBool = BooleanVal(false)

  def isNull = false

  def isUndefined = true

  def copy = this

  def incr = IntegerVal(1)

  def decr = NullVal

  def getAt(index: Value) = this
}
