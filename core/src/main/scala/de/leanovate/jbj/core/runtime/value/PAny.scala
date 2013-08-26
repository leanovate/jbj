package de.leanovate.jbj.core.runtime.value

import de.leanovate.jbj.core.runtime.context.Context

trait PAny {
  def toOutput(implicit ctx: Context): String

  def asVal: PVal

  def asVar: PVar

  def retain() {}

  def release()(implicit ctx:Context) {}

  def ==(other: PAny): PAny = BooleanVal(asVal.compare(other.asVal) == 0)

  def ===(other:PAny): PAny = (this.asVal, other.asVal) match {
    case (BooleanVal(leftVal), BooleanVal(rightVal)) => BooleanVal(leftVal == rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => BooleanVal(leftVal == rightVal)
    case (DoubleVal(leftVal), DoubleVal(rightVal)) => BooleanVal(leftVal == rightVal)
    case (StringVal(leftVal), StringVal(rightVal)) => BooleanVal(leftVal == rightVal)
    case (NullVal, NullVal) => BooleanVal.TRUE
    case _ => BooleanVal.FALSE
  }

  def !==(other:PAny): PAny = (this.asVal, other.asVal) match {
    case (BooleanVal(leftVal), BooleanVal(rightVal)) => BooleanVal(leftVal != rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => BooleanVal(leftVal != rightVal)
    case (DoubleVal(leftVal), DoubleVal(rightVal)) => BooleanVal(leftVal != rightVal)
    case (StringVal(leftVal), StringVal(rightVal)) => BooleanVal(leftVal != rightVal)
    case (NullVal, NullVal) => BooleanVal.FALSE
    case _ => BooleanVal.TRUE
  }

  def !=(other: PAny): PAny = BooleanVal(asVal.compare(other.asVal) != 0)

  def <(other: PAny): PAny = {
    val comp = asVal.compare(other.asVal)
    BooleanVal(comp < 0 && comp != Int.MinValue)
  }

  def <=(other: PAny): PAny = {
    val comp = asVal.compare(other.asVal)
    BooleanVal(comp <= 0 && comp != Int.MinValue)
  }

  def >(other: PAny): PAny = BooleanVal(asVal.compare(other.asVal) > 0)

  def >=(other: PAny): PAny = BooleanVal(asVal.compare(other.asVal) >= 0)

  def +(other: PAny): PAny = this.asVal.toNum + other.asVal.toNum

  def -(other: PAny): PAny = this.asVal.toNum - other.asVal.toNum

  def *(other: PAny): PAny = this.asVal.toNum * other.asVal.toNum

  def /(other: PAny): PAny = this.asVal.toNum / other.asVal.toNum

  def %(other: PAny): PAny = this.asVal.toInteger % other.asVal.toInteger

  def !(other: PAny): PAny = this.asVal.toStr ! other.asVal.toStr

  def &(other: PAny): PAny = (this.asVal, other.asVal) match {
    case (leftVal: StringVal, rightVal: StringVal) => leftVal & rightVal
    case (leftVal, rightVal) => leftVal.toInteger & rightVal.toInteger
  }

  def |(other: PAny): PAny = (this.asVal, other.asVal) match {
    case (leftVal: StringVal, rightVal: StringVal) => leftVal | rightVal
    case (leftVal, rightVal) => leftVal.toInteger | rightVal.toInteger
  }

  def ^(other: PAny): PAny = (this.asVal, other.asVal) match {
    case (leftVal: StringVal, rightVal: StringVal) => leftVal ^ rightVal
    case (leftVal, rightVal) => leftVal.toInteger ^ rightVal.toInteger
  }

  def <<(other:PAny):PAny =  this.asVal.toInteger << other.asVal.toInteger

  def >>(other:PAny):PAny =  this.asVal.toInteger >> other.asVal.toInteger

  def unary_-(): NumericVal = -asVal.toNum

  def unary_+(): NumericVal = asVal.toNum

  def unary_~(): PAny = this.asVal match {
    case value: StringVal => ~value
    case value => ~value.toInteger
  }

}
