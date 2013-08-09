package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.{BooleanVal, StringVal, IntegerVal}
import de.leanovate.jbj.runtime.PFunction

object StringFunctions {
  val functions: Seq[PFunction] = Seq(
    BuildinFunction1("strlen", {
      case (_, _, Some(str)) => IntegerVal(str.toStr.asString.length)
    }),
    BuildinFunction3("strstr", {
      case (_, _, Some(haystack), Some(needle), beforeNeedle) =>
        val needleStr = needle match {
          case str: StringVal => str.asString
          case int: IntegerVal => int.asLong.toChar.toString
        }
        val idx = haystack.toStr.asString.indexOf(needleStr)
        if (idx < 0) {
          BooleanVal.FALSE
        } else if (beforeNeedle.exists(_.toBool.asBoolean)) {
          StringVal(haystack.toStr.asString.substring(0, idx))
        } else {
          StringVal(haystack.toStr.asString.substring(idx))
        }
    }),
    BuildinFunction1("strtolower", {
      case (_, _,Some(str)) => StringVal(str.toStr.asString.toLowerCase)
    }),
    BuildinFunction1("strtoupper", {
      case (_, _, Some(str)) => StringVal(str.toStr.asString.toUpperCase)
    })
  )
}
