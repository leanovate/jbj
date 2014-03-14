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
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.adapter.GlobalFunctions

trait StringFunctions {
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

  @GlobalFunction(parameterMode = ParameterMode.STRICT_WARN, warnResult = NullVal)
  def str_replace(search: PVal, replace: PVal, subject: PVal, count: Option[PVar])(implicit ctx: Context): PVal = {
    val searchReplaces: Seq[(String, String)] = search.asVal.concrete match {
      case searchArray: ArrayVal =>
        replace.asVal.concrete match {
          case replaceArray: ArrayVal =>
            searchArray.keyValues.map {
              case (key, value) => value.asVal.concrete.toStr.asString -> replaceArray.getAt(key).map(_.asVal.concrete.toStr.asString).getOrElse("")
            }
          case replaceStr =>
            searchArray.keyValues.map {
              case (_, value) => value.asVal.concrete.toStr.asString -> replaceStr.toStr.asString
            }
        }
      case searchStr =>
        replace.asVal.concrete match {
          case replaceArray: ArrayVal =>
            Seq(searchStr.toStr.asString -> replaceArray.getAt(0).map(_.asVal.concrete.toStr.asString).getOrElse(""))
          case replaceStr =>
            Seq(searchStr.toStr.asString -> replaceStr.toStr.asString)
        }
    }
    var replaceCounter = 0
    val result = subject.asVal.concrete match {
      case subjectStr =>
        var current = subjectStr.toStr.asString

        searchReplaces.foreach {
          case (searchStr, replaceStr) =>
            val result = replaceWithCount(current, searchStr, replaceStr)
            current = result._1
            replaceCounter += result._2
        }
        StringVal(current)
    }
    count.foreach {
      ref =>
        ref.value = IntegerVal(replaceCounter)
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

  private def replaceWithCount(str: String, target: String, replacement: String): (String, Int) = {
    var counter = 0
    var start = 0
    var idx = str.indexOf(target)
    val result = new StringBuilder

    while (idx >= 0) {
      result.append(str.substring(start, idx))
      result.append(replacement)
      counter += 1
      start = idx + target.length
      idx = str.indexOf(target, start)
    }
    result.append(str.substring(start))
    (result.toString(), counter)
  }
}

object StringFunctions extends StringFunctions {
  val functions = GlobalFunctions.generatePFunctions(this)
}