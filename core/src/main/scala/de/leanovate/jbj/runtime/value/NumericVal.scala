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

  def getAt(index: Value) = UndefinedVal
}

object NumericVal {
  val intPattern = Pattern.compile( """(\-)?[0-9]*""")

  val floatPattern = Pattern.compile( """(\-)?[0-9]*(\.[0-9]*)?([eE][0-9]+)?""")

  def unapply(numeric: Value) = numeric.unref match {
    case IntegerVal(value) => Some(value.toDouble)
    case FloatVal(value) => Some(value)
    case BooleanVal(value) => Some(if (value) 1.0 else 0.0)
    case StringVal(value) if floatPattern.matcher(value).matches() => Some(value.toDouble)
    case _ => None
  }

}