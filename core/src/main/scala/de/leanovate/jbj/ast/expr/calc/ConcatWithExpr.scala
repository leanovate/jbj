package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.Context

case class ConcatWithExpr(reference: Reference, expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    val result = reference.eval.toStr dot expr.eval.toStr
    reference.assign(result)
    result
  }
}