package de.leanovate.jbj.core.runtime.buildin

import de.leanovate.jbj.core.runtime.value.PVal
import de.leanovate.jbj.core.runtime.annotations.GlobalFunction
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.api.JbjSettings
import java.util

object RuntimeFunctions extends WrappedFunctions {

  @GlobalFunction
  def error_reporting(value: Int)(implicit ctx: Context) {
    val errorReporing = JbjSettings.ErrorLevel.values().foldLeft(util.EnumSet.noneOf(classOf[JbjSettings.ErrorLevel])) {
      (set, enum) => if ((enum.getValue & value) != 0) {
        set.add(enum)
      }
        set
    }
    ctx.settings.setErrorReporting(errorReporing)
  }

  @GlobalFunction
  def define(name: String, value: PVal, caseInsensitive: Option[Boolean])(implicit ctx: Context) {
    ctx.global.defineConstant(name, value, caseInsensitive.getOrElse(false))
  }

  def main(args: Array[String]) {
    println(functions)
  }
}
