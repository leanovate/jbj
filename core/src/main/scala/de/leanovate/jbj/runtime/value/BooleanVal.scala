package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.{Context, ArrayKey}
import de.leanovate.jbj.ast.NodePosition

abstract class BooleanVal extends Value {
  def asBoolean: Boolean

  override def toBool = this

  override def toNum = toInteger

  override def toArray = ArrayVal(None -> this)

  override def isNull = false

  override def copy = this

  override def incr = this

  override def decr = this

  override def getAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition) = None

  override def setAt(index: Option[ArrayKey], value: Value)(implicit ctx: Context, position: NodePosition) {}
}

object BooleanVal {
  val TRUE = new BooleanVal {
    val asBoolean = true

    override def toOutput = "1"

    override def toDouble = DoubleVal(1.0)

    override val toInteger = IntegerVal(1)

    override val toStr = StringVal("1")
  }

  val FALSE = new BooleanVal {
    val asBoolean = false

    override def toOutput = ""

    override def toDouble = DoubleVal(0.0)

    override val toInteger = IntegerVal(0)

    override val toStr = StringVal("")
  }

  def apply(value: Boolean): BooleanVal = if (value) TRUE else FALSE

  def unapply(boolean: BooleanVal) = Some(boolean.asBoolean)
}
