package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.UndefinedVal

object RuntimeFunctions {
  val functions = Seq(
    BuildinFunction1("error_reporting", {
      case _ => UndefinedVal
    }))
}
