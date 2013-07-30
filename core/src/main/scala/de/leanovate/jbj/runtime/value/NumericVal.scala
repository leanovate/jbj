package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Value

trait NumericVal extends Value {
  def toNum: NumericVal = this

  def toDouble: Double

  def toLong: Long

  def toInt: Int

  def isNull = false

  def isUndefined = false

  def unref = this

  def copy = this

  def neg: NumericVal

  def getAt(index: Value) = UndefinedVal

  def +(other: Value): Value = (this, other) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal > 0 && leftVal <= Long.MaxValue - rightVal =>
      IntegerVal(leftVal + rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal <= 0 && leftVal >= Long.MinValue - rightVal =>
      IntegerVal(leftVal + rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => FloatVal(leftVal + rightVal)
  }

  def -(other: Value): Value = (this, other) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal > 0 && leftVal >= Long.MinValue + rightVal =>
      IntegerVal(leftVal - rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal <= 0 && leftVal <= Long.MaxValue + rightVal =>
      IntegerVal(leftVal - rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => FloatVal(leftVal - rightVal)
  }

  def *(other:Value):Value = (this, other) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal == 0 => IntegerVal(0)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal > 0 && leftVal <= Long.MaxValue / rightVal && leftVal >= Long.MinValue / rightVal => IntegerVal(leftVal * rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal < 0 && leftVal <= Long.MinValue / rightVal && leftVal >= Long.MaxValue / rightVal => IntegerVal(leftVal * rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => FloatVal(leftVal * rightVal)
  }

  def /(other:Value) :Value = (this, other) match {
    case (NumericVal(leftVal), NumericVal(rightVal)) => FloatVal(leftVal / rightVal)
  }
}

object NumericVal {
  val numericPattern = """[ ]*(\-?[0-9]*(\.[0-9]*)?([eE][0-9]+)?).*""".r

  def unapply(numeric: Value) = numeric.unref match {
    case IntegerVal(value) => Some(value.toDouble)
    case FloatVal(value) => Some(value)
    case BooleanVal(value) => Some(if (value) 1.0 else 0.0)
    case StringVal(numericPattern(num, _, _)) if !num.isEmpty => Some(num.toDouble)
    case _ => None
  }

}