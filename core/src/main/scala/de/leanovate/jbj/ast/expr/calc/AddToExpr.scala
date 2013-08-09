package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.Context

case class AddToExpr(reference: Reference, expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    val result = reference.eval.toNum + expr.eval.toNum
    reference.assignRef(result)
    result
  }
}
