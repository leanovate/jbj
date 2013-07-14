package de.leanovate.jbj.ast.value

import de.leanovate.jbj.ast.Value

case class NullVal() extends Value {
  def toNumeric = IntegerVal(0)
}
