/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

trait PAny {
  def toOutput(implicit ctx: Context): String

  def asVal: PVal

  def asVar: PVar

  def retain() {}

  def release()(implicit ctx: Context) {}

  def :==(other: PAny)(implicit ctx: Context): PVal = BooleanVal(asVal.compare(other.asVal) == 0)

  def ===(other: PAny): PVal = (this.asVal, other.asVal) match {
    case (BooleanVal(leftVal), BooleanVal(rightVal)) => BooleanVal(leftVal == rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => BooleanVal(leftVal == rightVal)
    case (DoubleVal(leftVal), DoubleVal(rightVal)) => BooleanVal(leftVal == rightVal)
    case (StringVal(leftVal), StringVal(rightVal)) => BooleanVal(leftVal == rightVal)
    case (NullVal, NullVal) => BooleanVal.TRUE
    case _ => BooleanVal.FALSE
  }

  def !==(other: PAny): PVal = (this.asVal, other.asVal) match {
    case (BooleanVal(leftVal), BooleanVal(rightVal)) => BooleanVal(leftVal != rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => BooleanVal(leftVal != rightVal)
    case (DoubleVal(leftVal), DoubleVal(rightVal)) => BooleanVal(leftVal != rightVal)
    case (StringVal(leftVal), StringVal(rightVal)) => BooleanVal(leftVal != rightVal)
    case (NullVal, NullVal) => BooleanVal.FALSE
    case _ => BooleanVal.TRUE
  }

  def :!=(other: PAny)(implicit ctx: Context): PVal = BooleanVal(asVal.compare(other.asVal) != 0)

  def <(other: PAny)(implicit ctx: Context): PVal = {
    val comp = asVal.compare(other.asVal)
    BooleanVal(comp < 0 && comp != Int.MinValue)
  }

  def <=(other: PAny)(implicit ctx: Context): PVal = {
    val comp = asVal.compare(other.asVal)
    BooleanVal(comp <= 0 && comp != Int.MinValue)
  }

  def >(other: PAny)(implicit ctx: Context): PVal = BooleanVal(asVal.compare(other.asVal) > 0)

  def >=(other: PAny)(implicit ctx: Context): PVal = BooleanVal(asVal.compare(other.asVal) >= 0)

  def +(other: PAny): PVal = this.asVal.toNum + other.asVal.toNum

  def -(other: PAny): PVal = this.asVal.toNum - other.asVal.toNum

  def *(other: PAny): PVal = this.asVal.toNum * other.asVal.toNum

  def /(other: PAny): PVal = this.asVal.toNum / other.asVal.toNum

  def %(other: PAny): PVal = this.asVal.toInteger % other.asVal.toInteger

  def !(other: PAny)(implicit ctx: Context): PVal = this.asVal.toStr ! other.asVal.toStr

  def &(other: PAny): PVal = (this.asVal, other.asVal) match {
    case (leftVal: StringVal, rightVal: StringVal) => leftVal & rightVal
    case (leftVal, rightVal) => leftVal.toInteger & rightVal.toInteger
  }

  def |(other: PAny): PVal = (this.asVal, other.asVal) match {
    case (leftVal: StringVal, rightVal: StringVal) => leftVal | rightVal
    case (leftVal, rightVal) => leftVal.toInteger | rightVal.toInteger
  }

  def ^(other: PAny): PVal = (this.asVal, other.asVal) match {
    case (leftVal: StringVal, rightVal: StringVal) => leftVal ^ rightVal
    case (leftVal, rightVal) => leftVal.toInteger ^ rightVal.toInteger
  }

  def <<(other: PAny): PVal = this.asVal.toInteger << other.asVal.toInteger

  def >>(other: PAny): PVal = this.asVal.toInteger >> other.asVal.toInteger

  def unary_!(): BooleanVal = BooleanVal(!asVal.toBool.asBoolean)

  def unary_-(): NumericVal = -asVal.toNum

  def unary_+(): NumericVal = asVal.toNum

  def unary_~(): PVal = this.asVal match {
    case value: StringVal => ~value
    case value => ~value.toInteger
  }

}
