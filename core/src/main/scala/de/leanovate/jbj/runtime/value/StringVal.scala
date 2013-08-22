package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context

class StringVal(var chars: Array[Byte]) extends PVal with ArrayLike {
  def asString(implicit ctx: Context) = new String(chars, ctx.settings.charset)

  override def toOutput(implicit ctx: Context) = asString

  override def toStr(implicit ctx: Context): StringVal = this

  override def toNum(implicit ctx: Context): NumericVal = asString match {
    case NumericVal.numericPattern(num, null, null) if !num.isEmpty && num != "-" && num != "." =>
      IntegerVal(num.toLong)
    case NumericVal.numericPattern(num, _, _) if !num.isEmpty && num != "-" && num != "." =>
      DoubleVal(num.toDouble)
    case _ => IntegerVal(0)
  }

  override def toDouble(implicit ctx: Context): DoubleVal = asString match {
    case NumericVal.numericPattern(num, _, _) if !num.isEmpty && num != "-" && num != "." =>
      DoubleVal(num.toDouble)
    case _ => DoubleVal(0.0)
  }

  override def toInteger(implicit ctx: Context): IntegerVal = asString match {
    case NumericVal.integerPattern(num) => IntegerVal(num.toLong)
    case _ => IntegerVal(0)
  }

  override def toBool(implicit ctx: Context): BooleanVal = BooleanVal(!asString.isEmpty)

  override def toArray(implicit ctx: Context) = ArrayVal(None -> this)

  override def isNull = false

  override def copy(implicit ctx: Context) = this

  override def incr = this

  override def decr = this

  override def typeName = "string"

  override def size: Int = chars.length

  override def compare(other: PVal)(implicit ctx: Context): Int = other match {
    case otherStr: StringVal if isStrongNumericPattern && otherStr.isStrongNumericPattern =>
      new String(chars).toDouble.compare(new String(chars).toDouble)
    case otherStr: StringVal => StringVal.compare(chars, otherStr.chars)
    case NumericVal(otherNum) =>
      this match {
        case NumericVal(thisNum) => thisNum.compare(otherNum)
        case _ => StringVal.compare(chars, otherNum.toString.getBytes)
      }
    case _ => StringVal.compare(chars, other.toStr.chars)
  }

  def isStrongNumericPattern: Boolean = {
    var idx = 0
    var sign = false
    var digits = false
    var dot = false
    var exp = false

    while (idx < chars.length) {
      val ch = chars(idx)
      if (ch == '+'.toByte || ch == '-'.toByte) {
        if (sign || digits || dot || exp)
          return false
        sign = true
      } else if (ch == '.'.toByte) {
        if (dot || exp)
          return false
        dot = true
      } else if (ch == 'e'.toByte || ch == 'E'.toByte) {
        if (exp || !digits)
          return false
        exp = true
      } else if (ch >= '0'.toByte && ch <= '9'.toByte) {
        digits = true
      } else {
        return false
      }

      idx += 1
    }
    digits
  }

  override def getAt(index: Long)(implicit ctx: Context) = {
    Some(StringVal(Array(chars(index.toInt))))
  }

  override def getAt(index: String)(implicit ctx: Context) = {
    Some(StringVal(Array(chars(0))))
  }

  override def setAt(index: Long, value: PAny)(implicit ctx: Context) {
    if (index < 0) {
      ctx.log.warn("Illegal string offset:  %d".format(index))
    } else {
      val chs = value.asVal.toStr.chars
      val ch: Byte = if (!chs.isEmpty) chs(0) else 0

      if (index >= chars.length) {
        val newChars = new Array[Byte](index.toInt + 1)
        Array.copy(chars, 0, newChars, 0, chars.length)
        for (i <- Range(chars.length, index.toInt))
          newChars(i) = 0x20.toByte
        newChars(index.toInt) = ch
        chars = newChars
      } else {
        chars(index.toInt) = ch
      }
    }
  }

  override def setAt(index: String, value: PAny)(implicit ctx: Context) {
    setAt(0, value)
  }

  override def append(value: PAny)(implicit ctx: Context) {
    throw new FatalErrorJbjException("[] operator not supported for strings")
  }

  override def unsetAt(index: Long)(implicit ctx: Context) {
    throw new FatalErrorJbjException("Cannot unset string offsets")
  }

  override def unsetAt(index: String)(implicit ctx: Context) {
    throw new FatalErrorJbjException("Cannot unset string offsets")
  }

  def __(other: StringVal): PVal = StringVal(Array.concat(chars, other.chars))

  def &(other: StringVal): PVal = {
    val resultLength = Math.min(chars.length, other.chars.length)
    val result = new Array[Byte](resultLength)
    for (i <- Range(0, resultLength)) {
      result(i) = (this.chars(i) & other.chars(i)).toByte
    }
    StringVal(result)
  }

  def |(other: StringVal): PVal = {
    val resultLength = Math.max(chars.length, other.chars.length)
    val result = new Array[Byte](resultLength)
    val leftIt = this.chars.iterator
    val rightIt = other.chars.iterator
    for (i <- Range(0, resultLength)) {
      val left = if (leftIt.hasNext) leftIt.next() else 0
      val right = if (rightIt.hasNext) rightIt.next() else 0
      result(i) = (left | right).toByte
    }
    StringVal(result)
  }

  def ^(other: StringVal): PVal = {
    val resultLength = Math.min(chars.length, other.chars.length)
    val result = new Array[Byte](resultLength)
    for (i <- Range(0, resultLength)) {
      result(i) = (this.chars(i) ^ other.chars(i)).toByte
    }
    StringVal(result)
  }

  def unary_~(): PVal = {
    val result = new Array[Byte](chars.length)
    for (i <- Range(0, result.length)) {
      result(i) = (~chars(i)).toByte
    }
    StringVal(result)
  }

  override def toXml =
    <string>
      {new String(chars, "UTF-8")}
    </string>
}

object StringVal {
  def apply(chars: Array[Byte]) = new StringVal(chars)

  def apply(str: String)(implicit ctx: Context) = new StringVal(str.getBytes(ctx.settings.charset))

  def unapply(str: StringVal)(implicit ctx: Context): Option[String] = Some(str.asString)

  def compare(v1: Array[Byte], v2: Array[Byte]): Int = {
    val len = Math.min(v1.length, v2.length)

    var k = 0

    while (k < len) {
      val c1 = v1(k)
      val c2 = v2(k)
      if (c1 != c2) {
        return (c1 & 0xff) - (c2 & 0xff)
      }
      k += 1
    }
    v1.length - v2.length
  }
}