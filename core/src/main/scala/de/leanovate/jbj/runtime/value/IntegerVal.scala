package de.leanovate.jbj.runtime.value


case class IntegerVal(value: Long) extends NumericVal {
  override def toOutput = value.toString

  override def toStr: StringVal = StringVal(value.toString)

  override def toDouble: DoubleVal = DoubleVal(value)

  override def toInteger: IntegerVal = this

  override def toBool = BooleanVal(value != 0)

  override def incr = IntegerVal(value + 1)

  override def decr = IntegerVal(value - 1)

  override def unary_- = if (value > Long.MinValue) IntegerVal(-value) else DoubleVal(-value.toDouble)

  def %(other: Value): Value = (this, other) match {
    case (_, IntegerVal(0)) => BooleanVal.FALSE
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => IntegerVal(leftVal % rightVal)
  }
}
