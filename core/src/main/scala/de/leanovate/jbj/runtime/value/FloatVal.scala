package de.leanovate.jbj.runtime.value

import java.io.PrintStream

case class FloatVal(value: Double) extends NumericVal {
  def toOutput(out: PrintStream) {
    out.print(value)
  }

  def toDump(out: PrintStream, ident: String = "") {
    out.println( """%sfloat(%s)""".format(ident, value.toString))
  }

  def toStr: StringVal = StringVal(value.toString)

  def toDouble: Double = value

  def toLong = value.toLong

  def toInt = value.toInt

  def toBool = BooleanVal(value != 0.0)

  def neg = FloatVal(-value)

  def incr = FloatVal(value + 1)

  def decr = FloatVal(value - 1)
}
