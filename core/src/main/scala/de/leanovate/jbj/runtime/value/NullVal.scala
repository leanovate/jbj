package de.leanovate.jbj.ast.value

import java.io.PrintStream
import de.leanovate.jbj.runtime.Value

object NullVal extends Value {
  def toOutput(out: PrintStream) {
  }

  def toStr = StringVal("")

  def toNum = IntegerVal(0)

  def toBool = BooleanVal(false)

  def isNull = true

  def isUndefined = false

  def copy = this

  def incr = IntegerVal(1)

  def decr = NullVal
}
