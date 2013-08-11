package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.{Context, ArrayKey}

trait Value extends ValueOrRef {
  def toOutput: String

  def toStr: StringVal

  def toNum: NumericVal

  def toInteger: IntegerVal

  def toDouble: DoubleVal

  def toBool: BooleanVal

  def toArray: ArrayVal

  def isNull: Boolean

  def copy: Value

  def incr: Value

  def decr: Value

  final override def value = this

  final override def incrRefCount() {}

  final override def decrRefCount() {}
}

object Value {
  def compare(left: Value, right: Value): Int = (left, right) match {
    case (StringVal(leftVal), StringVal(rightVal)) => leftVal.compare(rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => leftVal.compare(rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => leftVal.compare(rightVal)
    case (BooleanVal(leftVal), BooleanVal(rightVal)) => leftVal.compare(rightVal)
    case (anyLeft, anyRight) => anyLeft.toStr.asString.compare(anyRight.toStr.asString)
  }
}