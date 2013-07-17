package de.leanovate.jbj.ast.value

import de.leanovate.jbj.ast.Value
import java.io.PrintStream

object UndefinedVal extends Value {
  def toOutput(out: PrintStream) {
  }

  def toStr = StringVal("")

  def toNum = IntegerVal(0)

  def toBool = BooleanVal(false)

  def isNull = false

  def isUndefined = true

  def copy = this

  def incr = IntegerVal(1)

  def decr = NullVal
}
