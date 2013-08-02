package de.leanovate.jbj.runtime.value

import java.io.PrintStream

case class DoubleVal(value: Double) extends NumericVal {
  def toOutput(out: PrintStream) {
    out.print(value)
  }

  def toDump(out: PrintStream, ident: String = "") {
    out.println( """%sfloat(%s)""".format(ident, value.toString))
  }

  def toStr: StringVal = StringVal(value.toString)

  def toInteger: IntegerVal = IntegerVal(value.toLong)

  def toBool = BooleanVal(value != 0.0)

  def incr = DoubleVal(value + 1)

  def decr = DoubleVal(value - 1)

  def unary_- = DoubleVal(-value)
}
