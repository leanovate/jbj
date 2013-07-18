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
}

object BooleanVal {
  val TRUE = new BooleanVal {
    val value = true

    def toOutput(out: PrintStream) {
      out.print("1")
    }

    val toNum = IntegerVal(1)

    val toStr = StringVal("1")
  }

  val FALSE = new BooleanVal {
    val value = false

    def toOutput(out: PrintStream) {}

    val toNum = IntegerVal(0)

    val toStr = StringVal("")
  }

  def apply(value: Boolean): BooleanVal = if (value) TRUE else FALSE


}
