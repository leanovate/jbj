package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.NullVal

class ValueRef(private var current: Option[Value] = None) {

  def value = current.getOrElse(NullVal)

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
