package de.leanovate.jbj.ast.value

import de.leanovate.jbj.ast.Value
import java.io.PrintStream
import de.leanovate.jbj.exception.FatalErrorException

case class ArrayVal(keyValue: List[(Value, Value)]) extends Value {
  def toOutput(out: PrintStream) {
    out.print("Array")
  }

  def toStr = StringVal("Array")

  def toNum = {
    throw new FatalErrorException("Invalid conversion")
  }

  def isNull = false

  def isUndefined = false

  def copy = ArrayVal(List(keyValue: _*))
}
