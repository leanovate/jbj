package de.leanovate.jbj.runtime.value

import java.io.PrintStream

case class IntegerVal(value: Long) extends NumericVal {
  def toOutput(out: PrintStream) {
    out.print(value)
  }

  def toDump(out: PrintStream, ident: String = "") {
    out.println( """%sint(%d)""".format(ident, value))
  }

  def toStr: StringVal = StringVal(value.toString)

  def toDouble: Double = value

  def toBool = BooleanVal(value != 0)

  def incr = IntegerVal(value + 1)

  def decr = IntegerVal(value - 1)
}
