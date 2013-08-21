package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

abstract class BooleanVal extends PVal {
  def asBoolean: Boolean

  override def toBool(implicit ctx: Context) = this

  override def toNum(implicit ctx: Context) = toInteger

  override def toArray(implicit ctx: Context) = ArrayVal(None -> this)

  override def isNull = false

  override def copy(implicit ctx: Context) = this

  override def incr = this

  override def decr = this

  override def typeName = "boolean"
}

object BooleanVal {
  val TRUE = new BooleanVal {
    val asBoolean = true

    override def toOutput(implicit ctx: Context) = "1"

    override def toDouble(implicit ctx: Context) = DoubleVal(1.0)

    override def toInteger(implicit ctx: Context) = IntegerVal(1)

    override def toStr(implicit ctx: Context) = StringVal("1")

    override def compare(other: PVal)(implicit ctx: Context): Int = other match {
      case BooleanVal(otherBool) => if (otherBool) 0 else 1
      case NumericVal(otherDouble) => 1.0.compare(otherDouble)
      case str:StringVal => if (str.chars.length > 0) 0 else 1
      case array: ArrayVal => if (!array.isEmpty) 0 else 1
      case _ => 1
    }

    override def toXml = <true/>
  }

  val FALSE = new BooleanVal {
    val asBoolean = false

    override def toOutput(implicit ctx: Context) = ""

    override def toDouble(implicit ctx: Context) = DoubleVal(0.0)

    override def toInteger(implicit ctx: Context) = IntegerVal(0)

    override def toStr(implicit ctx: Context) = StringVal(Array.emptyByteArray)

    override def compare(other: PVal)(implicit ctx: Context): Int = other match {
      case BooleanVal(otherBool) => if (otherBool) -1 else 0
      case NumericVal(otherDouble) => 0.0.compare(otherDouble)
      case str:StringVal => if (str.chars.length > 0) -1 else 0
      case array: ArrayVal => if (!array.isEmpty) -1 else 0
      case _ => 0
    }

    override def toXml = <false/>
  }

  def apply(value: Boolean): BooleanVal = if (value) TRUE else FALSE

  def unapply(boolean: BooleanVal) = Some(boolean.asBoolean)
}
