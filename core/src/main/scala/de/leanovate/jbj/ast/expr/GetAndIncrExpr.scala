package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Reference, Expr}
import de.leanovate.jbj.runtime.Context

case class GetAndIncrExpr(reference: Reference) extends Expr {
  def eval(implicit ctx: Context) = {
    val result = reference.eval
    reference.assignRef(result.incr)
    result
  }
}
