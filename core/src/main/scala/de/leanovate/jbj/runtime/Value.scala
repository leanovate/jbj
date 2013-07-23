package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{IntegerVal, BooleanVal, StringVal, NumericVal}
import java.io.PrintStream
import de.leanovate.jbj.ast.Expr

trait Value {
  def toOutput(out: PrintStream)

  def toDump(out: PrintStream, ident: String = "")

  def toStr: StringVal

  def toNum: NumericVal

  def toBool: BooleanVal

  def isNull: Boolean

  def isUndefined: Boolean

  def copy: Value

  def incr: Value

  def decr: Value
}

object Value {
  def compare(left: Value, right: Value): Int = (left, right) match {
    case (StringVal(leftVal), StringVal(rightVal)) => leftVal.compare(rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => leftVal.compare(rightVal)
    case (anyLeft, anyRight) => anyLeft.toNum.toDouble.compare(anyRight.toNum.toDouble)
  }
}