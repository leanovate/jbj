package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Context


case class IntegerVal(asLong: Long) extends NumericVal {
  override def toOutput(implicit ctx: Context) = asLong.toString

  override def toStr(implicit ctx: Context): StringVal = StringVal(asLong.toString)

  override def toDouble(implicit ctx: Context): DoubleVal = DoubleVal(asLong)

  override def toInteger(implicit ctx: Context): IntegerVal = this

  override def toBool(implicit ctx: Context) = BooleanVal(asLong != 0)

  override def incr = if (asLong < Long.MaxValue) IntegerVal(asLong + 1) else DoubleVal(asLong.toDouble + 1)

  override def decr = if (asLong > Long.MinValue) IntegerVal(asLong - 1) else DoubleVal(asLong.toDouble - 1)

  override def unary_- = if (asLong > Long.MinValue) IntegerVal(-asLong) else DoubleVal(-asLong.toDouble)

  def asInt = asLong.toInt

  def &(other: IntegerVal): Value = IntegerVal(this.asLong & other.asLong)

  def |(other: IntegerVal): Value = IntegerVal(this.asLong | other.asLong)

  def ^(other: IntegerVal): Value = IntegerVal(this.asLong ^ other.asLong)

  def unary_~(): Value = IntegerVal(~asLong)

  def %(other: Value): Value = (this, other) match {
    case (_, IntegerVal(0)) => BooleanVal.FALSE
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => IntegerVal(leftVal % rightVal)
  }
}
