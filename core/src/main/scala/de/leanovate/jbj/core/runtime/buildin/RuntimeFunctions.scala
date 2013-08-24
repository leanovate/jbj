package de.leanovate.jbj.core.runtime.buildin

import de.leanovate.jbj.core.runtime.value.PVal
import de.leanovate.jbj.core.runtime.annotations.GlobalFunction
import de.leanovate.jbj.core.runtime.context.Context

object RuntimeFunctions extends WrappedFunctions {

  @GlobalFunction
  def error_reporting(value: Int)(implicit ctx: Context) {
    ctx.settings.errorReporting = value
  }

  @GlobalFunction
  def define(name: String, value: PVal, caseInsensitive: Option[Boolean])(implicit ctx: Context) {
    ctx.defineConstant(name, value, caseInsensitive.getOrElse(false))
  }

  def main(args: Array[String]) {
    println(functions)
  }
}
