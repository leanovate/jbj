package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import de.leanovate.jbj.runtime.{ArrayKey, Value}

abstract class BooleanVal extends Value {
  def value: Boolean

  override def toBool = this

  override def toNum = toInteger

  override def isNull = false

  override def isUndefined = false

  override def copy = this

  override def incr = this

  override def decr = this

  override def getAt(index: ArrayKey) = UndefinedVal

  override def setAt(index: ArrayKey, value: Value) {}
}

object BooleanVal {
  val TRUE = new BooleanVal {
    val value = true

    override def toOutput(out: PrintStream) {
      out.print("1")
    }

    override def toDump(out: PrintStream, ident: String = "") {
      out.println("%sbool(true)".format(ident))
    }

    override val toInteger = IntegerVal(1)

    override val toStr = StringVal("1")
  }

  val FALSE = new BooleanVal {
    val value = false

    override def toOutput(out: PrintStream) {}

    override def toDump(out: PrintStream, ident: String = "") {
      out.println("%sbool(false)".format(ident))
    }

    override val toInteger = IntegerVal(0)

    override val toStr = StringVal("")
  }

  def apply(value: Boolean): BooleanVal = if (value) TRUE else FALSE

  def unapply(boolean: BooleanVal) = Some(boolean.value)
}
