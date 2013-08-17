package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Context

trait PVal extends PAny {
  def toOutput(implicit ctx:Context): String

  def toStr(implicit ctx:Context): StringVal

  def toNum(implicit ctx:Context): NumericVal

  def toInteger(implicit ctx:Context): IntegerVal

  def toDouble(implicit ctx:Context): DoubleVal

  def toBool(implicit ctx:Context): BooleanVal

  def toArray(implicit ctx:Context): ArrayVal

  def isNull: Boolean

  def copy(implicit ctx:Context): PVal

  def incr: PVal

  def decr: PVal

  final override def asVal = this

  final override def asVar = PVar(this)

  final override def incrRefCount() {}

  final override def decrRefCount() {}
}

object PVal {
  def compare(left: PVal, right: PVal)(implicit ctx:Context): Int = (left, right) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => leftVal.compare(rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => leftVal.compare(rightVal)
    case (BooleanVal(leftVal), BooleanVal(rightVal)) => leftVal.compare(rightVal)
    case (anyLeft, anyRight) => anyLeft.toStr.asString.compare(anyRight.toStr.asString)
  }
}