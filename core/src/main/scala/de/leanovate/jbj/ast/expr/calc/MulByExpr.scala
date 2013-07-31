package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.Context

case class MulByExpr(reference: Reference, expr: Expr) extends Expr {
  override def eval(ctx: Context) = {
    val result = reference.eval(ctx).toNum * expr.eval(ctx).toNum
    reference.assign(ctx, result)
    result
  }
}