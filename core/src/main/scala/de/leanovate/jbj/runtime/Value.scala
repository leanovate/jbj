package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.value.{IntegerVal, BooleanVal, StringVal, NumericVal}
import java.io.PrintStream
import de.leanovate.jbj.ast.Expr

trait Value extends Expr {
  def eval(ctx: Context): Value = this

  def toOutput(out: PrintStream)

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
  def compare(left:Value, right:Value ) :Int = (left, right) match {
    case (StringVal(leftVal), StringVal(rightVal)) => leftVal.compare(rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => leftVal.compare(rightVal)
    case (anyLeft, anyRight) => anyLeft.toNum.toDouble.compare(anyRight.toNum.toDouble)
  }
}