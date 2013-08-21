package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.value.{PAny, PVal, NullVal}
import de.leanovate.jbj.runtime.{PFunction}
import de.leanovate.jbj.ast.{Expr, NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.context.Context

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
