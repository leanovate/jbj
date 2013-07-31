package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.Context

case class ConcatWithExpr(reference: Reference, expr: Expr) extends Expr {
  override def eval(ctx: Context) = {
    val result = reference.eval(ctx).toStr dot expr.eval(ctx).toStr
    reference.assign(ctx, result)
    result
  }
}