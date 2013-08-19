package de.leanovate.jbj.runtime.value

import scala.Predef._
import java.math.MathContext
import java.math
import de.leanovate.jbj.runtime.Context

case class DoubleVal(asDouble: Double) extends NumericVal {
  override def toOutput(implicit ctx: Context) = compatbleStr

  override def toStr(implicit ctx: Context): StringVal = StringVal(compatbleStr)

  override def toDouble(implicit ctx: Context) = this

  override def toInteger(implicit ctx: Context): IntegerVal =
    if (asDouble > Long.MinValue.toDouble && asDouble < Long.MaxValue.toDouble)
      IntegerVal(asDouble.toLong)
    else
      IntegerVal(Long.MinValue)

  override def toBool(implicit ctx: Context) = BooleanVal(asDouble != 0.0)

  override def incr = DoubleVal(asDouble + 1)

  override def decr = DoubleVal(asDouble - 1)

  override def typeName = "double"

  override def compare(other: PVal)(implicit ctx: Context): Int = other match {
    case NumericVal(otherDouble) => asDouble.compare(otherDouble)
    case _ => StringVal.compare(asDouble.toString.getBytes, other.toStr.chars)
  }

  override def unary_- = DoubleVal(-asDouble)

  private def compatbleStr = {
    val str = new math.BigDecimal(asDouble, DoubleVal.mathContext).toString
    if (str.indexOf('.') >= 0) {
      val idx = str.indexOf('E')
      if (idx < 0)
        str.reverse.dropWhile(_ == '0').reverse
      else {
        str.substring(0, idx).reverse.dropWhile(_ == '0').dropWhile(_ == '.').reverse + str.substring(idx)
      }
    } else
      str
  }
}

object DoubleVal {
  val mathContext = new MathContext(14)
}