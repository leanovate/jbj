package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.Context

case class AssignExpr(reference: Reference, expr: Expr) extends BinaryExpr {
  def left = reference

  def right = expr

  override def eval(implicit ctx: Context) = {
    val value = expr.eval
    reference.assignRef(value)
    value
  }
}
