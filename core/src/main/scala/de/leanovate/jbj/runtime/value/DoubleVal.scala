package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import scala.Predef._
import java.math.MathContext
import java.math

case class DoubleVal(value: Double) extends NumericVal {
  override def toOutput = compatbleStr

  override def toStr: StringVal = StringVal(compatbleStr)

  override def toDouble = this

  override def toInteger: IntegerVal = IntegerVal(value.toLong)

  override def toBool = BooleanVal(value != 0.0)

  override def incr = DoubleVal(value + 1)

  override def decr = DoubleVal(value - 1)

  def unary_- = DoubleVal(-value)

  private def compatbleStr = {
    val str = new math.BigDecimal(value, DoubleVal.mathContext).toString
    if (str.indexOf('.') >= 0 && str.indexOf("E") < 0)
      str.reverse.dropWhile(_ == '0').reverse
    else
      str
  }
}

object DoubleVal {
  val mathContext = new MathContext(14)
}