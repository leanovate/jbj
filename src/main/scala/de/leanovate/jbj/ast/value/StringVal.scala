package de.leanovate.jbj.ast.value

import de.leanovate.jbj.ast.Value

case class StringVal(value: String) extends Value {
  def toNumeric = if (value.contains(".")) FloatVal(value.toDouble) else IntegerVal(value.toInt)
}
