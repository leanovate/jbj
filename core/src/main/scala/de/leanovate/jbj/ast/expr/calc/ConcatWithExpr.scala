package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.runtime.Context

case class ConcatWithExpr(reference: ReferableExpr, expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    val result = reference.eval.toStr dot expr.eval.toStr
    reference.assignRef(result)
    result
  }
}