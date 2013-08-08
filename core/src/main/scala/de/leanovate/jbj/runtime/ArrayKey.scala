package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{Value, NumericVal, IntegerVal, StringVal}

sealed trait ArrayKey {
  def value: Value
}

case class StringArrayKey(key: String) extends ArrayKey {
  def value = StringVal(key)
}

case class IntArrayKey(key: Long) extends ArrayKey {
  def value = IntegerVal(key)
}

object ArrayKey {
  def apply(value: Value): Option[ArrayKey] = value match {
    case IntegerVal(idx) => Some(IntArrayKey(idx.toInt))
    case NumericVal(idx) => Some(IntArrayKey(idx.toInt))
    case StringVal(idx) => Some(StringArrayKey(idx))
    case _ => None
  }
}