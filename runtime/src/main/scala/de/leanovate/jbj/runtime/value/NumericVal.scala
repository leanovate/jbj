/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException


trait NumericVal extends PConcreteVal {
  override def toNum(implicit ctx: Context): NumericVal = this

  override def toArray(implicit ctx: Context) = ArrayVal(None -> this)

  override def isScalar = true

  override def isNull = false

  override def copy = this

  def unary_- : NumericVal

  def +(other: PVal): PVal = (this, other) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal > 0 && leftVal <= Long.MaxValue - rightVal =>
      IntegerVal(leftVal + rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal <= 0 && leftVal >= Long.MinValue - rightVal =>
      IntegerVal(leftVal + rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => DoubleVal(leftVal + rightVal)
  }

  def -(other: PVal): PVal = (this, other) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal > 0 && leftVal >= Long.MinValue + rightVal =>
      IntegerVal(leftVal - rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal <= 0 && leftVal <= Long.MaxValue + rightVal =>
      IntegerVal(leftVal - rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => DoubleVal(leftVal - rightVal)
  }

  def *(other: PVal): PVal = (this, other) match {
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal == 0 => IntegerVal(0)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal == Long.MinValue && leftVal == 1 => IntegerVal(Long.MinValue)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal > 0 && leftVal <= Long.MaxValue / rightVal && leftVal >= Long.MinValue / rightVal => IntegerVal(leftVal * rightVal)
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if rightVal < 0 && leftVal <= -Long.MaxValue / rightVal && leftVal >= Long.MaxValue / rightVal => IntegerVal(leftVal * rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => DoubleVal(leftVal * rightVal)
  }

  def /(other: PVal): PVal = (this, other) match {
    case (NumericVal(leftVal), NumericVal(0.0)) => BooleanVal.FALSE
    case (IntegerVal(leftVal), IntegerVal(rightVal)) if (leftVal != Long.MinValue || rightVal != -1) && leftVal % rightVal == 0 => IntegerVal(leftVal / rightVal)
    case (NumericVal(leftVal), NumericVal(rightVal)) => DoubleVal(leftVal / rightVal)
  }

  override def isCallable(implicit ctx: Context) = false

  override def call(params: List[PParam])(implicit ctx: Context) =
    throw new FatalErrorJbjException("Function name must be a string")
}

object NumericVal {
  val integerPattern = """[ ]*([\+\-]?[0-9]+).*""".r
  val numericPattern = """[ ]*([\+\-]?[0-9]*(\.[0-9]*)?([eE][0-9]+)?).*""".r
  val truePattern = "[tT][rR][uU][eE]".r
  val falsePattern = "[fF][aA][lL][sS][eE]".r

  def unapply(numeric: PVal): Option[Double] = numeric match {
    case IntegerVal(value) => Some(value.toDouble)
    case DoubleVal(value) => Some(value)
    case BooleanVal(value) => Some(if (value) 1.0 else 0.0)
    case StringVal(numericPattern(num, _, _)) if !num.isEmpty && num != "-" && num != "." => Some(num.toDouble)
    case StringVal(truePattern()) => Some(1.0)
    case StringVal(falsePattern()) => Some(1.0)
    case array: ArrayVal => Some(if (!array.isEmpty) 1.0 else 0.0)
    case _ => None
  }
}
