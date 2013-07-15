package de.leanovate.jbj.ast.value

import de.leanovate.jbj.ast.Value
import java.io.PrintStream

case class StringVal(value: String) extends Value {
  def toOutput(out: PrintStream) {
    out.print(value)
  }

  def toStr: StringVal = this

  def toNum: NumericVal = if (value.contains(".")) FloatVal(value.toDouble) else IntegerVal(value.toInt)

  def isNull = false

  def isUndefined = false
}
