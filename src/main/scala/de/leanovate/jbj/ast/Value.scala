package de.leanovate.jbj.ast

import de.leanovate.jbj.ast.value.{IntegerVal, BooleanVal, StringVal, NumericVal}
import de.leanovate.jbj.exec.Context
import java.io.PrintStream

trait Value extends Expr {
  def eval(ctx: Context): Value = this

  def toOutput(out: PrintStream)

  def toStr: StringVal

  def toNum: NumericVal

  def toBool: BooleanVal

  def isNull: Boolean

  def isUndefined: Boolean

  def copy: Value
}

object Value {
  def compare(left:Value, right:Value ) :Int = (left, right) match {
    case (StringVal(leftVal), StringVal(rightVal)) => leftVal.compare(rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => leftVal.compare(rightVal)
    case (anyLeft, anyRight) => anyLeft.toNum.toDouble.compare(anyRight.toNum.toDouble)
  }
}