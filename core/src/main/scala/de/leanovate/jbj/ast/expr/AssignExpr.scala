package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.runtime.Context

case class AssignExpr(reference: ReferableExpr, expr: Expr) extends BinaryExpr {
  def left = reference

  def right = expr

  override def eval(implicit ctx: Context) = {
    val value = expr.eval
    reference.assignVar(value)
    value
  }
}
