package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.Context

case class SubFromExpr(reference: Reference, expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    val result = reference.eval.toNum - expr.eval.toNum
    reference.assign(result)
    result
  }
}