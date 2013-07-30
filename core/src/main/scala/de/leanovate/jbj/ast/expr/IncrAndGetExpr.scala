package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Reference, Expr}
import de.leanovate.jbj.runtime.Context

case class IncrAndGetExpr(reference: Reference) extends Expr {
  def eval(ctx: Context) = {
    val result = reference.eval(ctx).incr
    reference.assign(ctx, result)
    result
  }
}
