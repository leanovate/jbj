/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.buildin

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.runtime.context.Context

object StringFunctions {

  @GlobalFunction
  def strlen(str: Array[Byte]): Int = str.length

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
  def bin2hex(str: Array[Byte]): Array[Byte] = {
    val result = new Array[Byte](str.length * 2)
    for (i <- Range(0, str.length)) {
      val ch = str(i)
      result(i * 2) = Character.forDigit((ch >> 4) & 0xf, 16).toByte
      result(i * 2 + 1) = Character.forDigit(ch & 0xf, 16).toByte
    }
    result
  }

  @GlobalFunction
  def trim(pVal: PVal)(implicit ctx: Context): PVal = {
    pVal.concrete match {
      case StringVal(str) =>
        StringVal(str.trim)
      case v =>
        ctx.log.warn("trim() expects parameter 1 to be array, %s given".format(v.typeName))
        NullVal
    }
  }
}
