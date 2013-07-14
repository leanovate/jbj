package de.leanovate.jbj.ast.value

import de.leanovate.jbj.ast.Value
import java.io.PrintStream

case class NullVal() extends Value {
  def toOutput(out: PrintStream) {
  }

  def toStr = StringVal("")

  def toNum = IntegerVal(0)
}
