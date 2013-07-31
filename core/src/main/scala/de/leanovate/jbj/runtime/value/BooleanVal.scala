package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import de.leanovate.jbj.runtime.Value

abstract class BooleanVal extends Value {
  def value: Boolean

  def toBool = this

  def isNull = false

  def isUndefined = false

  def copy = this

  def incr = this

  def decr = this

  def getAt(index: Value) = UndefinedVal
}

object BooleanVal {
  val TRUE = new BooleanVal {
    val value = true

    def toOutput(out: PrintStream) {
      out.print("1")
    }

    def toDump(out: PrintStream, ident: String = "") {
      out.println("%sbool(true)".format(ident))
    }

    val toNum = IntegerVal(1)

    val toStr = StringVal("1")
  }

  val FALSE = new BooleanVal {
    val value = false

    def toOutput(out: PrintStream) {}

    def toDump(out: PrintStream, ident: String = "") {
      out.println("%sbool(false)".format(ident))
    }

    val toNum = IntegerVal(0)

    val toStr = StringVal("")
  }

  def apply(value: Boolean): BooleanVal = if (value) TRUE else FALSE

  def unapply(boolean: BooleanVal) = Some(boolean.value)
}
