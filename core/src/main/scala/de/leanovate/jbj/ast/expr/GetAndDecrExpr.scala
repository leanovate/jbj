package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Reference, Expr}
import de.leanovate.jbj.runtime.Context

case class GetAndDecrExpr(reference: Reference) extends Expr {
  override def eval(implicit ctx: Context) = {
    val result = reference.eval
    reference.assign(result.decr)
    result
  }
}
