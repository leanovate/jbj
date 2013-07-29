package de.leanovate.jbj.ast.name

import de.leanovate.jbj.ast.{Name, Expr}
import de.leanovate.jbj.runtime.Context

case class DynamicName(expr: Expr) extends Name {
  def evalName(ctx: Context) = expr.eval(ctx).toStr.value
}
