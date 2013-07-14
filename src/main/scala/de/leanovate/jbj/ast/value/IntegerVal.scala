package de.leanovate.jbj.ast.value

import java.io.PrintStream

case class IntegerVal(value: Int) extends NumericVal {
  def toOutput(out: PrintStream) {
    out.print(value)
  }

  def toStr: StringVal = StringVal(value.toString)

  def toDouble: Double = value
}
