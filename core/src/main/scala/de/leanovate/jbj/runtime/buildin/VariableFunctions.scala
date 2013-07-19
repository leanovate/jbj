package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.BooleanVal

object VariableFunctions {
  val functions = Seq(
    BuildinFunction1("isset", {
      value => BooleanVal(!value.isUndefined)
    }))
}
