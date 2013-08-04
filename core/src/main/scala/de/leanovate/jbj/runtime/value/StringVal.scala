package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import de.leanovate.jbj.runtime.{Context, IntArrayKey, ArrayKey, Value}
import de.leanovate.jbj.ast.NodePosition

case class StringVal(value: String) extends Value {
  override def toOutput(out: PrintStream) {
    out.print(value)
  }

  override def toDump(out: PrintStream, ident: String = "") {
    out.println( """%sstring(%s) "%s"""".format(ident, value.length, value))
  }

  override def toStr: StringVal = this

  override def toNum: NumericVal = value match {
    case NumericVal.numericPattern(num, null, null) if !num.isEmpty => IntegerVal(num.toLong)
    case NumericVal.numericPattern(num, _, _) if !num.isEmpty => DoubleVal(num.toDouble)
    case _ => IntegerVal(0)
  }

  override def toInteger: IntegerVal = value match {
    case NumericVal.integerPattern(num) => IntegerVal(num.toLong)
    case _ => IntegerVal(0)
  }

  override def toBool: BooleanVal = BooleanVal(!value.isEmpty)

  override def toArray = ArrayVal(None -> this)

  override def isNull = false

  override def copy = this

  override def incr = this

  override def decr = this

  override def getAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition) = index match {
    case IntArrayKey(idx) => Some(StringVal(value(idx.toInt).toString))
    case _ => Some(StringVal(value(0).toString))
  }

  override def setAt(index: Option[ArrayKey], value: Value)(implicit ctx: Context, position: NodePosition) {}

  def dot(other: Value): Value = StringVal(value + other.toStr.value)
}
