package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.UndefinedVal

object RuntimeFunctions {
  val error_reporting = BuildinFunction1("error_reporting", {
    value => UndefinedVal
  })
}
