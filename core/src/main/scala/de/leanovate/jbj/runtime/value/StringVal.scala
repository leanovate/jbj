package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import de.leanovate.jbj.runtime.Value

case class StringVal(value: String) extends Value {
  def toOutput(out: PrintStream) {
    out.print(value)
  }

  def toDump(out: PrintStream, ident: String = "") {
    out.println( """%sstring(%s) "%s"""".format(ident, value.length, value))
  }

  def toStr: StringVal = this

  def toNum: NumericVal = value match {
    case NumericVal.numericPattern(num, null, null) if !num.isEmpty => IntegerVal(num.toInt)
    case NumericVal.numericPattern(num, _, _) if !num.isEmpty => FloatVal(num.toDouble)
    case _ => IntegerVal(0)
  }

  def toBool: BooleanVal = BooleanVal(!value.isEmpty)

  def isNull = false

  def isUndefined = false

  def unref = this

  def copy = this

  def incr = this

  def decr = this

  def getAt(index: Value) = index.unref match {
    case IntegerVal(idx) => StringVal(value(idx.toInt).toString)
    case NumericVal(idx) => StringVal(value(idx.toInt).toString)
    case _ => UndefinedVal
  }
}
