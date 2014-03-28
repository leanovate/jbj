/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context


case class IntegerVal(asLong: Long) extends NumericVal {
  override def toOutput(implicit ctx: Context) = asLong.toString

  override def toStr(implicit ctx: Context): StringVal = StringVal(asLong.toString.getBytes("UTF-8"))

  override def toDouble: DoubleVal = DoubleVal(asLong)

  override def toInteger(implicit ctx: Context): IntegerVal = this

  override def toBool = BooleanVal(asLong != 0)

  override def incr(implicit ctx: Context) = if (asLong < Long.MaxValue) IntegerVal(asLong + 1) else DoubleVal(asLong.toDouble + 1)

  override def decr(implicit ctx: Context) = if (asLong > Long.MinValue) IntegerVal(asLong - 1) else DoubleVal(asLong.toDouble - 1)

  override def typeName(simple: Boolean = false) = "integer"

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

  def <<(other:IntegerVal):PVal =  IntegerVal(this.asLong << other.asLong)

  def >>(other:IntegerVal):PVal = IntegerVal(this.asLong >> other.asLong)

  override def unary_~()(implicit ctx: Context): PVal = IntegerVal(~asLong)

  def %(other: PVal): PVal = (this, other) match {
    case (_, IntegerVal(0)) => BooleanVal.FALSE
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => IntegerVal(leftVal % rightVal)
  }

  override def toXml = <int value={asLong.toString}/>
}
