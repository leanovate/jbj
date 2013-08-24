package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

trait PAny {
  def toOutput(implicit ctx: Context): String

  def asVal: PVal

  def asVar: PVar

  def retain() {}

  def release() {}

  def ==(other: PAny): PAny = BooleanVal(asVal.compare(other.asVal) == 0)

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

  def unary_-(): NumericVal = -asVal.toNum

  def unary_+(): NumericVal = asVal.toNum

  def unary_~(): PAny = this.asVal match {
    case value: StringVal => ~value
    case value => ~value.toInteger
  }

}
