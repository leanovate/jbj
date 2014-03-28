/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

trait PAny {
  def toOutput(implicit ctx: Context): String = asVal.toOutput

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

  def +(other: PAny)(implicit ctx: Context): PVal = (this.asVal, other.asVal) match {
    case (left: ArrayVal, right: ArrayVal) => left + right
    case (_: ArrayVal, o) =>
      o.toNum
      throw new FatalErrorJbjException("Unsupported operand types")
    case (o, _: ArrayVal) =>
      o.toNum
      throw new FatalErrorJbjException("Unsupported operand types")
    case (left, right) => left.toNum + right.toNum
  }

  def -(other: PAny)(implicit ctx: Context): PVal = this.asVal.toNum - other.asVal.toNum

  def *(other: PAny)(implicit ctx: Context): PVal = this.asVal.toNum * other.asVal.toNum

  def /(other: PAny)(implicit ctx: Context): PVal = this.asVal.toNum / other.asVal.toNum

  def %(other: PAny)(implicit ctx: Context): PVal = this.asVal.toInteger % other.asVal.toInteger

  def !!(other: PAny)(implicit ctx: Context): PVal = this.asVal.toStr !! other.asVal.toStr

  def &(other: PAny)(implicit ctx: Context): PVal = (this.asVal, other.asVal) match {
    case (leftVal: StringVal, rightVal: StringVal) => leftVal & rightVal
    case (leftVal, rightVal) => leftVal.toInteger & rightVal.toInteger
  }

  def |(other: PAny)(implicit ctx: Context): PVal = (this.asVal, other.asVal) match {
    case (leftVal: StringVal, rightVal: StringVal) => leftVal | rightVal
    case (leftVal, rightVal) => leftVal.toInteger | rightVal.toInteger
  }

  def ^(other: PAny)(implicit ctx: Context): PVal = (this.asVal, other.asVal) match {
    case (leftVal: StringVal, rightVal: StringVal) => leftVal ^ rightVal
    case (leftVal, rightVal) => leftVal.toInteger ^ rightVal.toInteger
  }

  def <<(other: PAny)(implicit ctx: Context): PVal = this.asVal.toInteger << other.asVal.toInteger

  def >>(other: PAny)(implicit ctx: Context): PVal = this.asVal.toInteger >> other.asVal.toInteger

  def unary_!(): BooleanVal = BooleanVal(!asVal.toBool.asBoolean)

  def unary_-()(implicit ctx: Context): NumericVal = -asVal.toNum

  def unary_+()(implicit ctx: Context): NumericVal = asVal.toNum

  def unary_~()(implicit ctx: Context): PVal = this.asVal match {
    case value: StringVal => ~value
    case value => ~value.toInteger
  }

  def isCallable(implicit ctx: Context): Boolean = asVal.isCallable

  def call(params: List[PParam])(implicit ctx: Context): PAny = asVal.call(params)
}