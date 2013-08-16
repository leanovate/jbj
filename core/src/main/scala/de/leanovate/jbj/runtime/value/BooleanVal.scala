package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Context

abstract class BooleanVal extends PVal {
  def asBoolean: Boolean

  override def toBool(implicit ctx: Context) = this

  override def toNum(implicit ctx: Context) = toInteger

  override def toArray(implicit ctx: Context) = ArrayVal(None -> this)

  override def isNull = false

  override def copy = this

  override def incr = this

  override def decr = this
}

object BooleanVal {
  val TRUE = new BooleanVal {
    val asBoolean = true

    override def toOutput(implicit ctx: Context) = "1"

    override def toDouble(implicit ctx: Context) = DoubleVal(1.0)

    override def toInteger(implicit ctx: Context) = IntegerVal(1)

    override def toStr(implicit ctx: Context) = StringVal("1")
  }

  val FALSE = new BooleanVal {
    val asBoolean = false

    override def toOutput(implicit ctx: Context) = ""

    override def toDouble(implicit ctx: Context) = DoubleVal(0.0)

    override def toInteger(implicit ctx: Context) = IntegerVal(0)

    override def toStr(implicit ctx: Context) = StringVal(Array.emptyByteArray)
  }

  def apply(value: Boolean): BooleanVal = if (value) TRUE else FALSE

  def unapply(boolean: BooleanVal) = Some(boolean.asBoolean)
}
