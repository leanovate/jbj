package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.{ArrayVal, IntegerVal}

object ArrayFunctions {
  val functions = Seq(
    BuildinFunction1("count", {
      case Some(array : ArrayVal) => array.count
      case Some(_) => IntegerVal(1)
    })
  )
}
