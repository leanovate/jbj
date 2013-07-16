package de.leanovate.jbj.ast.value

import de.leanovate.jbj.ast.Value
import java.io.PrintStream

object UndefinedVal extends Value {
  def toOutput(out: PrintStream) {
  }

  def toStr = StringVal("")

  def toNum = IntegerVal(0)

  def isNull = false

  def isUndefined = true

  def copy = this
}
