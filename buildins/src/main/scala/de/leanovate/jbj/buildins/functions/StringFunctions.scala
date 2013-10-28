/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.annotations.{ParameterMode, GlobalFunction}
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.runtime.context.Context
import java.net.URLEncoder

object StringFunctions {
  @GlobalFunction
  def bin2hex(str: Array[Byte]): Array[Byte] = {
    val result = new Array[Byte](str.length * 2)
    for (i <- Range(0, str.length)) {
      val ch = str(i)
      result(i * 2) = Character.forDigit((ch >> 4) & 0xf, 16).toByte
      result(i * 2 + 1) = Character.forDigit(ch & 0xf, 16).toByte
    }
    result
  }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN, warnResult = NullVal)
  def strcasecmp(str1: String, str2: String): Int = {
    str1.toLowerCase.compareTo(str2.toLowerCase)
  }

  @GlobalFunction
  def sprintf(format: String, args: PVal*)(implicit ctx: Context): String = {
    format.format(args.map {
      case DoubleVal(d) => d
      case IntegerVal(v) => v
      case StringVal(str) => str
      case v => v.toStr.asString
    }: _*)
  }

  @GlobalFunction
  def strlen(str: Array[Byte]): Int = str.length

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN, warnResult = NullVal)
  def strncmp(str1: String, str2: String, len: Int)(implicit ctx: Context): PVal = {
    if (len < 0) {
      ctx.log.warn("Length must be greater than or equal to 0")
      BooleanVal.FALSE
    } else {
      IntegerVal(str1.take(len).compareTo(str2.take(len)))
    }
  }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN, warnResult = NullVal)
  def strncasecmp(str1: String, str2: String, len: Int)(implicit ctx: Context): PVal = {
    if (len < 0) {
      ctx.log.warn("Length must be greater than or equal to 0")
      BooleanVal.FALSE
    } else {
      IntegerVal(str1.take(len).toLowerCase.compareTo(str2.take(len).toLowerCase))
    }
  }

  @GlobalFunction
  def str_repeat(input: String, multiplier: Int): String = {
    input * multiplier
  }

  @GlobalFunction
  def strstr(haystack: Array[Byte], needle: PVal, beforeNeedle: Option[Boolean]): PVal = {
    val needleBytes = needle match {
      case str: StringVal => str.chars
      case int: IntegerVal => Array(int.asLong.toByte)
    }
    val idx = haystack.indexOfSlice(needleBytes)
    if (idx < 0) {
      BooleanVal.FALSE
    } else if (beforeNeedle.getOrElse(false)) {
      StringVal(haystack.take(idx))
    } else {
      StringVal(haystack.drop(idx))
    }
  }

  @GlobalFunction
  def strpos(haystack: Array[Byte], needle: PVal, offset: Option[Int]): PVal = {
    val needleBytes = needle match {
      case str: StringVal => str.chars
      case int: IntegerVal => Array(int.asLong.toByte)
    }
    val idx = haystack.indexOfSlice(needleBytes, offset.getOrElse(0))
    if (idx < 0) {
      BooleanVal.FALSE
    } else {
      IntegerVal(idx)
    }
  }

  @GlobalFunction
  def strtolower(str: String): String = str.toLowerCase

  @GlobalFunction
  def strtoupper(str: String): String = str.toUpperCase

  @GlobalFunction
  def trim(pVal: PVal)(implicit ctx: Context): PVal = {
    pVal.concrete match {
      case StringVal(str) =>
        StringVal(str.trim)
      case obj: ObjectVal =>
        StringVal(obj.toStr.asString.trim)
      case v =>
        ctx.log.warn("trim() expects parameter 1 to be string, %s given".format(v.typeName(simple = true)))
        NullVal
    }
  }

  @GlobalFunction
  def ucfirst(str: String): String = {
    if (str.isEmpty)
      ""
    else {
      str.take(1).toUpperCase + str.drop(1)
    }
  }

  @GlobalFunction
  def urlencode(str: String): String = {
    URLEncoder.encode(str, "UTF-8")
  }
}
