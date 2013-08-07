package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import scala.Predef._
import java.math.MathContext
import java.math

case class DoubleVal(value: Double) extends NumericVal {
  def toOutput(out: PrintStream) {
    out.print(compatbleStr)
  }

  def toDump(out: PrintStream, ident: String = "") {
    out.println( """%sfloat(%s)""".format(ident, compatbleStr))
  }

  def toStr: StringVal = StringVal(compatbleStr)

  def toInteger: IntegerVal = IntegerVal(value.toLong)

  def toBool = BooleanVal(value != 0.0)

  def incr = DoubleVal(value + 1)

  def decr = DoubleVal(value - 1)

  def unary_- = DoubleVal(-value)

  private def compatbleStr = {
    val str = new math.BigDecimal(value, DoubleVal.mathContext).toString
    if ( str.indexOf('.') >= 0 && str.indexOf("E") < 0)
      str.reverse.dropWhile(_ == '0').reverse
    else
      str
  }
}

object DoubleVal {
  val mathContext = new MathContext(14)
}