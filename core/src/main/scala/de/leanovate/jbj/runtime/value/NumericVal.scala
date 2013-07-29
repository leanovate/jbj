package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Value

trait NumericVal extends Value {
  def toNum: NumericVal = this

  def toDouble: Double

  def toLong: Long

  def toInt: Int

  def isNull = false

  def isUndefined = false

  def unref = this

  def copy = this

  def neg: NumericVal

  def getAt(index: Value) = UndefinedVal
}

object NumericVal {
  val numericPattern = """[ ]*(\-?[0-9]*(\.[0-9]*)?([eE][0-9]+)?).*""".r

  def unapply(numeric: Value) = numeric.unref match {
    case IntegerVal(value) => Some(value.toDouble)
    case FloatVal(value) => Some(value)
    case BooleanVal(value) => Some(if (value) 1.0 else 0.0)
    case StringVal(numericPattern(num, _, _)) if !num.isEmpty => Some(num.toDouble)
    case _ => None
  }

}