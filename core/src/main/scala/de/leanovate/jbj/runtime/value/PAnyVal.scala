package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.{Context, ArrayKey}

trait PAnyVal extends PAny {
  def toOutput(implicit ctx:Context): String

  def toStr(implicit ctx:Context): StringVal

  def toNum(implicit ctx:Context): NumericVal

  def toInteger(implicit ctx:Context): IntegerVal

  def toDouble(implicit ctx:Context): DoubleVal

  def toBool(implicit ctx:Context): BooleanVal

  def toArray(implicit ctx:Context): ArrayVal

  def isNull: Boolean

  def copy: PAnyVal

  def incr: PAnyVal

  def decr: PAnyVal

  final override def value = this

  final override def incrRefCount() {}

  final override def decrRefCount() {}
}

object PAnyVal {
  def compare(left: PAnyVal, right: PAnyVal)(implicit ctx:Context): Int = (left, right) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => leftVal.compare(rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => leftVal.compare(rightVal)
    case (BooleanVal(leftVal), BooleanVal(rightVal)) => leftVal.compare(rightVal)
    case (anyLeft, anyRight) => anyLeft.toStr.asString.compare(anyRight.toStr.asString)
  }
}