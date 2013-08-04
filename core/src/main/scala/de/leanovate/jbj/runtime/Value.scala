package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.ast.NodePosition

trait Value {
  def toOutput(out: PrintStream)

  def toDump(out: PrintStream, ident: String = "")

  def toStr: StringVal

  def toNum: NumericVal

  def toInteger: IntegerVal

  def toBool: BooleanVal

  def toArray: ArrayVal

  def isNull: Boolean

  def isUndefined: Boolean

  def copy: Value

  def incr: Value

  def decr: Value

  def getAt(index: ArrayKey): Value

  def setAt(index: Option[ArrayKey], value: Value)(implicit ctx: Context, position: NodePosition)
}

object Value {
  def compare(left: Value, right: Value): Int = (left, right) match {
    case (StringVal(leftVal), StringVal(rightVal)) => leftVal.compare(rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => leftVal.compare(rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => leftVal.compare(rightVal)
    case (BooleanVal(leftVal), BooleanVal(rightVal)) => leftVal.compare(rightVal)
    case (anyLeft, anyRight) => anyLeft.toStr.value.compare(anyRight.toStr.value)
  }
}