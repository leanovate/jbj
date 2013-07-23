package de.leanovate.jbj.runtime.value

import java.io.PrintStream

case class FloatVal(value: Double) extends NumericVal {
  def toOutput(out: PrintStream) {
    out.print(value)
  }

  def toDump(out: PrintStream, ident: String = "") {
    out.println( """%sfloat(%f)""".format(ident, value))
  }

  def toStr: StringVal = StringVal(value.toString)

  def toDouble: Double = value

  def toBool = BooleanVal(value != 0.0)

  def incr = FloatVal(value + 1)

  def decr = FloatVal(value - 1)
}
