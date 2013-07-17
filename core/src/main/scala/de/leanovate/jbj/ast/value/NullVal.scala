package de.leanovate.jbj.ast.value

import de.leanovate.jbj.ast.Value
import java.io.PrintStream

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
