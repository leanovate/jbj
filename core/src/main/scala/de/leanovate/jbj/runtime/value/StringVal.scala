package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.{Context, IntArrayKey, ArrayKey}
import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class StringVal(asString: String) extends Value with ArrayLike{
  override def toOutput = asString

  override def toStr: StringVal = this

  override def toNum: NumericVal = asString match {
    case NumericVal.numericPattern(num, null, null) if !num.isEmpty && num != "-" && num != "." =>
      IntegerVal(num.toLong)
    case NumericVal.numericPattern(num, _, _) if !num.isEmpty && num != "-" && num != "." =>
      DoubleVal(num.toDouble)
    case _ => IntegerVal(0)
  }

  override def toDouble: DoubleVal = asString match {
    case NumericVal.numericPattern(num, _, _) if !num.isEmpty && num != "-" && num != "." =>
      DoubleVal(num.toDouble)
    case _ => DoubleVal(0.0)
  }

  override def toInteger: IntegerVal = asString match {
    case NumericVal.integerPattern(num) => IntegerVal(num.toLong)
    case _ => IntegerVal(0)
  }

  override def toBool: BooleanVal = BooleanVal(!asString.isEmpty)

  override def toArray = ArrayVal(None -> this)

  override def isNull = false

  override def copy = this

  override def incr = this

  override def decr = this

  override def getAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition) = index match {
    case IntArrayKey(idx) => Some(StringVal(asString(idx.toInt).toString))
    case _ => Some(StringVal(asString(0).toString))
  }

  override def setAt(index: Option[ArrayKey], value: ValueOrRef)(implicit ctx: Context, position: NodePosition) {}

  override  def unsetAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition) {
    throw new FatalErrorJbjException("Cannot unset string offsets")
  }

  def dot(other: Value): Value = StringVal(asString + other.toStr.asString)
}
