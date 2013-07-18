package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import de.leanovate.jbj.runtime.Value

class ValueRef(private var current: Option[Value] = None) extends Value {
  def toOutput(out: PrintStream) {
    current.foreach(_.toOutput(out))
  }

  def toStr = current.map(_.toStr).getOrElse(StringVal(""))

  def toNum = current.map(_.toNum).getOrElse(IntegerVal(0))

  def toBool = current.map(_.toBool).getOrElse(BooleanVal.FALSE)

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

  def incr = value.incr

  def decr = value.decr
}

object ValueRef {
  def apply(v: Value): ValueRef = new ValueRef(Some(v))
}
