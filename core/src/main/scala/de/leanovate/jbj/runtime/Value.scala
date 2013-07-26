package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{IntegerVal, BooleanVal, StringVal, NumericVal}
import java.io.PrintStream

trait Value {
  def toOutput(out: PrintStream)

  def toDump(out: PrintStream, ident: String = "")

  def toStr: StringVal

  def toNum: NumericVal

  def toBool: BooleanVal

  def isNull: Boolean

  def isUndefined: Boolean

  def unref: Value

  def copy: Value

  def incr: Value

  def decr: Value

  def getAt(index: Value): Value
}

object Value {
  def compare(left: Value, right: Value): Int = (left.unref, right.unref) match {
    case (StringVal(leftVal), StringVal(rightVal)) => leftVal.compare(rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => leftVal.compare(rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => leftVal.compare(rightVal)
    case (anyLeft, anyRight) => anyLeft.toStr.value.compare(anyRight.toStr.value)
  }
}