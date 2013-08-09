package de.leanovate.jbj.runtime.value


case class IntegerVal(asLong: Long) extends NumericVal {
  override def toOutput = asLong.toString

  override def toStr: StringVal = StringVal(asLong.toString)

  override def toDouble: DoubleVal = DoubleVal(asLong)

  override def toInteger: IntegerVal = this

  override def toBool = BooleanVal(asLong != 0)

  override def incr = IntegerVal(asLong + 1)

  override def decr = IntegerVal(asLong - 1)

  override def unary_- = if (asLong > Long.MinValue) IntegerVal(-asLong) else DoubleVal(-asLong.toDouble)

  def %(other: Value): Value = (this, other) match {
    case (_, IntegerVal(0)) => BooleanVal.FALSE
    case (IntegerVal(leftVal), IntegerVal(rightVal)) => IntegerVal(leftVal % rightVal)
  }
}
