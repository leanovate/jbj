package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Reference, FilePosition, Expr}
import de.leanovate.jbj.runtime.Context

case class DecrAndGetExpr(position: FilePosition, reference: Reference) extends Expr {
  def eval(ctx: Context) = {
    val result = reference.eval(ctx).decr
    reference.assign(ctx, result)
    result
  }
}
