package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{Value, NumericVal, IntegerVal, StringVal}

sealed trait ArrayKey {
  def value(implicit ctx: Context): Value
}

case class StringArrayKey(key: String) extends ArrayKey {
  def value(implicit ctx: Context) = StringVal(key)
}

case class IntArrayKey(key: Long) extends ArrayKey {
  def value(implicit ctx: Context) = IntegerVal(key)
}

object ArrayKey {
  def apply(value: Value)(implicit ctx: Context): Option[ArrayKey] = value match {
    case IntegerVal(idx) => Some(IntArrayKey(idx.toInt))
    case NumericVal(idx) => Some(IntArrayKey(idx.toInt))
    case str: StringVal => Some(StringArrayKey(str.asString))
    case _ => None
  }
}