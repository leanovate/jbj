package de.leanovate.jbj.ast.value

import java.io.PrintStream
import de.leanovate.jbj.exception.FatalErrorException
import de.leanovate.jbj.runtime.Value

case class ArrayVal(keyValue: List[(Value, Value)]) extends Value {
  def toOutput(out: PrintStream) {
    out.print("Array")
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

  def copy = ArrayVal(List(keyValue: _*))

  def incr = this

  def decr = this
}
