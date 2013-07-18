package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.ast.value.IntegerVal

object StringFunctions {
  val strlen = BuildinFunction1("strlen", {
    str => IntegerVal(str.toStr.value.length)
  })
}
