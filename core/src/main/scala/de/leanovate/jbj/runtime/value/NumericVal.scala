package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Value

trait NumericVal extends Value {
  def toNum: NumericVal = this

  def toDouble: Double

  def isNull = false

  def isUndefined = false

  def copy = this
}

object NumericVal {
  def unapply(numeric: NumericVal) = Some(numeric.toDouble)
}