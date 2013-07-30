package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Reference, Expr}
import de.leanovate.jbj.runtime.Context

case class GetAndDecrExpr(reference: Reference) extends Expr {
  def eval(ctx: Context) = {
    val result = reference.eval(ctx)
    reference.assign(ctx, result.decr)
    result
  }
}
