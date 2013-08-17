package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

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

  override def copy = this

  override def incr = this

  override def decr = this

  override def getAt(index: Long)(implicit ctx: Context, position: NodePosition) = {
    Some(StringVal(Array(chars(index.toInt))))
  }

  override def getAt(index: String)(implicit ctx: Context, position: NodePosition) = {
    Some(StringVal(Array(chars(0))))
  }

  override def setAt(index: Long, value: PAny)(implicit ctx: Context, position: NodePosition) {
    if (index < 0) {
      ctx.log.warn(position, "Illegal string offset:  %d".format(index))
    } else {
      val chs = value.value.toStr.chars
      val ch: Byte = if (!chs.isEmpty) chars(0) else 0

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

  override def setAt(index: String, value: PAny)(implicit ctx: Context, position: NodePosition) {
    setAt(0, value)
  }

  override def append(value: PAny)(implicit ctx: Context, position: NodePosition) {
    throw new FatalErrorJbjException("[] operator not supported for strings")
  }

  override def unsetAt(index: Long)(implicit ctx: Context, position: NodePosition) {
    throw new FatalErrorJbjException("Cannot unset string offsets")
  }

  override def unsetAt(index: String)(implicit ctx: Context, position: NodePosition) {
    throw new FatalErrorJbjException("Cannot unset string offsets")
  }

  def dot(other: StringVal): PVal = StringVal(Array.concat(chars, other.chars))

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
}

object StringVal {
  def apply(chars: Array[Byte]) = new StringVal(chars)

  def apply(str: String)(implicit ctx: Context) = new StringVal(str.getBytes(ctx.settings.charset))

  def unapply(str: StringVal)(implicit ctx: Context): Option[String] = Some(str.asString)
}