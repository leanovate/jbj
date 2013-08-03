package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.{Context, ArrayKey, Value}
import de.leanovate.jbj.ast.NodePosition

trait NumericVal extends Value {
  override def toNum: NumericVal = this

  override def isNull = false

  override def isUndefined = false

  override def copy = this

  override def getAt(index: ArrayKey) = UndefinedVal

  override def setAt(index: Option[ArrayKey], value: Value)(implicit ctx: Context, position: NodePosition) {}

  def unary_- : NumericVal

  def +(other: Value): Value = (this, other) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal > 0 && leftVal <= Long.MaxValue - rightVal =>
      IntegerVal(leftVal + rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal <= 0 && leftVal >= Long.MinValue - rightVal =>
      IntegerVal(leftVal + rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => DoubleVal(leftVal + rightVal)
  }

  def -(other: Value): Value = (this, other) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal > 0 && leftVal >= Long.MinValue + rightVal =>
      IntegerVal(leftVal - rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal <= 0 && leftVal <= Long.MaxValue + rightVal =>
      IntegerVal(leftVal - rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => DoubleVal(leftVal - rightVal)
  }

  def *(other: Value): Value = (this, other) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal == 0 => IntegerVal(0)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal > 0 && leftVal <= Long.MaxValue / rightVal && leftVal >= Long.MinValue / rightVal => IntegerVal(leftVal * rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal < 0 && leftVal <= Long.MinValue / rightVal && leftVal >= Long.MaxValue / rightVal => IntegerVal(leftVal * rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => DoubleVal(leftVal * rightVal)
  }

  def /(other: Value): Value = (this, other) match {
    case (NumericVal(leftVal), NumericVal(rightVal)) => DoubleVal(leftVal / rightVal)
  }
}

object NumericVal {
  val integerPattern = """[ ]*([\+\-]?[0-9]+).*""".r
  val numericPattern = """[ ]*([\+\-]?[0-9]*(\.[0-9]*)?([eE][0-9]+)?).*""".r
  val truePattern = "[tT][rR][uU][eE]".r
  val falsePattern = "[fF][aA][lL][sS][eE]".r

  def unapply(numeric: Value): Option[Double] = numeric match {
    case IntegerVal(value) => Some(value.toDouble)
    case DoubleVal(value) => Some(value)
    case BooleanVal(value) => Some(if (value) 1.0 else 0.0)
    case StringVal(numericPattern(num, _, _)) if !num.isEmpty && num != "-" => Some(num.toDouble)
    case StringVal(truePattern()) => Some(1.0)
    case StringVal(falsePattern()) => Some(1.0)
    case array: ArrayVal => Some(if (!array.isEmpty) 1.0 else 0.0)
    case _ => None
  }

}