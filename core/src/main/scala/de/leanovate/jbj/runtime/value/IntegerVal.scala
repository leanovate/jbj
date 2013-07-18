package de.leanovate.jbj.runtime.value

import java.io.PrintStream

case class IntegerVal(value: Int) extends NumericVal {
  def toOutput(out: PrintStream) {
    out.print(value)
  }

  def toStr: StringVal = StringVal(value.toString)

  def toDouble: Double = value

  def toBool = BooleanVal(value != 0)

  def incr = IntegerVal(value + 1)

  def decr = IntegerVal(value - 1)
}
