package de.leanovate.jbj.ast.value

case class FloatVal(value: Double) extends NumericVal {
  def toDouble: Double = value
}
