package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.runtime.Context

case class GetAndDecrExpr(reference: ReferableExpr) extends Expr {
  override def eval(implicit ctx: Context) = {
    val result = reference.eval
    reference.assignVar(result.decr)
    result
  }
}
