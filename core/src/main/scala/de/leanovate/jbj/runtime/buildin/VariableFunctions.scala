package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.ast.value.BooleanVal

object VariableFunctions {
  val isset = BuildinFunction1("isset", {
    value => BooleanVal(!value.isUndefined)
  })
}
