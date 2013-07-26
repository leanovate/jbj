package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.{BooleanVal, StringVal, IntegerVal}

object StringFunctions {
  val functions = Seq(
    BuildinFunction1("strlen", {
      case Some(str) => IntegerVal(str.toStr.value.length)
    }),
    BuildinFunction3("strstr", {
      case (Some(haystack), Some(needle), beforeNeedle) =>
        val needleStr = needle match {
          case str: StringVal => str.value
          case int: IntegerVal => int.value.toChar.toString
        }
        val idx = haystack.toStr.value.indexOf(needleStr)
        if (idx < 0) {
          BooleanVal.FALSE
        } else if (beforeNeedle.map(_.toBool.value).getOrElse(false)) {
          StringVal(haystack.toStr.value.substring(0, idx))
        } else {
          StringVal(haystack.toStr.value.substring(idx))
        }
    }),
    BuildinFunction1("strtolower", {
      case Some(str) => StringVal(str.toStr.value.toLowerCase)
    }),
    BuildinFunction1("strtoupper", {
      case Some(str) => StringVal(str.toStr.value.toUpperCase)
    })
  )
}
