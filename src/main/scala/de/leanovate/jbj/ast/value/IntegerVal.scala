package de.leanovate.jbj.ast.value

case class IntegerVal(value: Int) extends NumericVal {
  def toDouble: Double = value
}
