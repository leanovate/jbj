package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.{BooleanVal, StringVal, IntegerVal}
import de.leanovate.jbj.runtime.Value

object StringFunctions {
  val functions = Seq(
    BuildinFunction1("strlen", {
      str => IntegerVal(str.toStr.value.length)
    }),
    BuildinFunction3("strstr", {
      (haystack: Value, needle: Value, beforeNeedle: Value) =>
        val needleStr = needle match {
          case str: StringVal => str.value
          case int: IntegerVal => int.value.toChar.toString
        }
        val idx = haystack.toStr.value.indexOf(needleStr)
        if (idx < 0) {
          BooleanVal.FALSE
        } else if (beforeNeedle.toBool.value) {
          StringVal(haystack.toStr.value.substring(0, idx))
        } else {
          StringVal(haystack.toStr.value.substring(idx))
        }
    }),
    BuildinFunction1("strtolower", {
      str => StringVal(str.toStr.value.toLowerCase)
    }),
    BuildinFunction1("strtoupper", {
      str => StringVal(str.toStr.value.toUpperCase)
    })
  )
}
