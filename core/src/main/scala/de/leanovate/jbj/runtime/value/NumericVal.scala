package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Value
import java.util.regex.Pattern

trait NumericVal extends Value {
  def toNum: NumericVal = this

  def toDouble: Double

  def isNull = false

  def isUndefined = false

  def unref = this

  def copy = this
}

object NumericVal {
  private val numericPattern = Pattern.compile( """[0-9]*(\.[0-9]*)?([eE][0-9]+)?""")

  def unapply(numeric: Value) = numeric.unref match {
    case IntegerVal(value) => Some(value.toDouble)
    case FloatVal(value) => Some(value)
    case StringVal(value) if numericPattern.matcher(value).matches() => Some(value.toDouble)
    case _ => None
  }

}