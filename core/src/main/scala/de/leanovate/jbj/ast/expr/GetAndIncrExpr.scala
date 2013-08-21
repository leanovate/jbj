package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.runtime.Context

case class GetAndIncrExpr(reference: ReferableExpr) extends Expr {
  def eval(implicit ctx: Context) = {
    val result = reference.evalOld
    reference.evalRef.assign(result.incr)
    result
  }
}
