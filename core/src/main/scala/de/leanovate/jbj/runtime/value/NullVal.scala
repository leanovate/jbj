package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

object NullVal extends PVal {
  override def toOutput(implicit ctx: Context) = ""

  override def toStr(implicit ctx: Context) = StringVal("")

  override def toNum(implicit ctx: Context) = toInteger

  override def toDouble(implicit ctx: Context) = DoubleVal(0.0)

  override def toInteger(implicit ctx: Context) = IntegerVal(0)

  override def toBool(implicit ctx: Context) = BooleanVal(false)

  override def toArray(implicit ctx: Context) = ArrayVal()

  override def isNull = true

  override def copy(implicit ctx: Context) = this

  override def incr = IntegerVal(1)

  override def decr = NullVal

  override def typeName = "null"

  override def compare(other: PVal)(implicit ctx: Context): Int = other match {
    case BooleanVal(otherBool) => if (otherBool) 1 else 0
    case NumericVal(otherDouble) => 0.0.compare(otherDouble)
    case _ => StringVal.compare(Array[Byte](), other.toStr.chars)
  }
}
