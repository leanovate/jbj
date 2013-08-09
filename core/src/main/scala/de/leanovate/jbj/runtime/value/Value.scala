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

  def getAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition): Option[Value]

  def setAt(index: Option[ArrayKey], value: Value)(implicit ctx: Context, position: NodePosition)

  final override def value = this
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