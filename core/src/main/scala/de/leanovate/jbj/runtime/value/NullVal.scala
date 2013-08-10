package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.{Context, ArrayKey}
import de.leanovate.jbj.ast.NodePosition

object NullVal extends Value {
  override def toOutput = ""

  override def toStr = StringVal("")

  override def toNum = toInteger

  override def toDouble = DoubleVal(0.0)

  override def toInteger = IntegerVal(0)

  override def toBool = BooleanVal(false)

  override def toArray = ArrayVal()

  override def isNull = true

  override def copy = this

  override def incr = IntegerVal(1)

  override def decr = NullVal

  override def getAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition) = None

  override def setAt(index: Option[ArrayKey], value: ValueOrRef)(implicit ctx: Context, position: NodePosition) {}
}
