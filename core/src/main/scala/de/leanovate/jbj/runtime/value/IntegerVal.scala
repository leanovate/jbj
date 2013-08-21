package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import scala.xml.NodeSeq


case class IntegerVal(asLong: Long) extends NumericVal {
  override def toOutput(implicit ctx: Context) = asLong.toString

  override def toStr(implicit ctx: Context): StringVal = StringVal(asLong.toString)

  override def toDouble(implicit ctx: Context): DoubleVal = DoubleVal(asLong)

  override def toInteger(implicit ctx: Context): IntegerVal = this

  override def toBool(implicit ctx: Context) = BooleanVal(asLong != 0)

  override def incr = if (asLong < Long.MaxValue) IntegerVal(asLong + 1) else DoubleVal(asLong.toDouble + 1)

  override def decr = if (asLong > Long.MinValue) IntegerVal(asLong - 1) else DoubleVal(asLong.toDouble - 1)

  override def typeName = "integer"

  override def compare(other: PVal)(implicit ctx: Context): Int = other match {
    case IntegerVal(otherLong) => asLong.compare(otherLong)
    case NumericVal(otherDouble) => asLong.toDouble.compare(otherDouble)
    case _ => StringVal.compare(asLong.toString.getBytes, other.toStr.chars)
  }

  override def unary_- = if (asLong > Long.MinValue) IntegerVal(-asLong) else DoubleVal(-asLong.toDouble)

  def asInt = asLong.toInt

  def &(other: IntegerVal): PVal = IntegerVal(this.asLong & other.asLong)

  def |(other: IntegerVal): PVal = IntegerVal(this.asLong | other.asLong)

  def ^(other: IntegerVal): PVal = IntegerVal(this.asLong ^ other.asLong)

  def unary_~(): PVal = IntegerVal(~asLong)

  def %(other: PVal): PVal = (this, other) match {
    case (_, IntegerVal(0)) => BooleanVal.FALSE
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => IntegerVal(leftVal % rightVal)
  }

  override def toXml = <int value={asLong.toString}/>
}
