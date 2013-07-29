package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.UndefinedVal

class ValueRef(private var current: Option[Value] = None) {

  def value = current.getOrElse(UndefinedVal)

  def value_=(v: Value) {
    current = Some(v)
  }

  def unset() {
    current = None
  }
}

object ValueRef {
  def apply(): ValueRef = new ValueRef(None)

  def apply(v: Value): ValueRef = new ValueRef(Some(v))
}
