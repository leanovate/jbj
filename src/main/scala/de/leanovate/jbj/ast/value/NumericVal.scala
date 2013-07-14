package de.leanovate.jbj.ast.value

import de.leanovate.jbj.ast.Value

trait NumericVal extends Value {
  def toNumeric: NumericVal = this

  def toDouble: Double
}

object NumericVal {
  def unapply(numeric: NumericVal) = Some(numeric.toDouble)
}