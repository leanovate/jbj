package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.{Context, ArrayKey}
import de.leanovate.jbj.ast.NodePosition

object NullVal extends PVal {
  override def toOutput(implicit ctx:Context) = ""

  override def toStr(implicit ctx:Context) = StringVal("")

  override def toNum(implicit ctx:Context) = toInteger

  override def toDouble(implicit ctx:Context) = DoubleVal(0.0)

  override def toInteger(implicit ctx:Context) = IntegerVal(0)

  override def toBool(implicit ctx:Context) = BooleanVal(false)

  override def toArray(implicit ctx:Context) = ArrayVal()

  override def isNull = true

  override def copy = this

  override def incr = IntegerVal(1)

  override def decr = NullVal
}
