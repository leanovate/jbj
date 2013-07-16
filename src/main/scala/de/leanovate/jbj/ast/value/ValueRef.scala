package de.leanovate.jbj.ast.value

import de.leanovate.jbj.ast.Value
import java.io.PrintStream


class ValueRef(private var current: Option[Value] = None) extends Value {
  def this(v: Value) = this(Some(v))

  def toOutput(out: PrintStream) {
    current.foreach(_.toOutput(out))
  }

  def toStr = current.map(_.toStr).getOrElse(StringVal(""))

  def toNum = current.map(_.toNum).getOrElse(IntegerVal(0))

  def toBool = current.map(_.toBool).getOrElse(BooleanVal(false))

  def isNull = current.exists(_.isNull)

  def isUndefined = !current.isDefined

  def value = current.getOrElse(UndefinedVal)

  def value_=(v: Value) {
    current = Some(v)
  }

  def unset() {
    current = None
  }

  def copy = value.copy
}
