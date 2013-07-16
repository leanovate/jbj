package de.leanovate.jbj.ast.value

import de.leanovate.jbj.ast.Value
import java.io.PrintStream

case class BooleanVal(value: Boolean) extends Value {
  def toOutput(out: PrintStream) {
    if (value) out.print("1")
  }

  def toStr = StringVal(if (value) "1" else "")

  def toNum = IntegerVal(if (value) 1 else 0)

  def toBool = this

  def isNull = false

  def isUndefined = false

  def copy = this
}
