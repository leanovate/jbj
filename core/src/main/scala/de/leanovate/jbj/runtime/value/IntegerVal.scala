package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import de.leanovate.jbj.runtime.Value

case class IntegerVal(value: Long) extends NumericVal {
  def toOutput(out: PrintStream) {
    out.print(value)
  }

  def toDump(out: PrintStream, ident: String = "") {
    out.println( """%sint(%d)""".format(ident, value))
  }

  def toStr: StringVal = StringVal(value.toString)

  def toInteger: IntegerVal = this

  def toBool = BooleanVal(value != 0)

  def incr = IntegerVal(value + 1)

  def decr = IntegerVal(value - 1)

  def unary_- = if (value > Long.MinValue) IntegerVal(-value) else DoubleVal(-value.toDouble)

  def %(other: Value): Value = (this, other) match {
    case (_, IntegerVal(0)) => BooleanVal.FALSE
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => IntegerVal(leftVal % rightVal)
  }
}
