package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.runtime.Context

case class IncrAndGetExpr(reference: ReferableExpr) extends Expr {
  override def eval(implicit ctx: Context) = {
    val result = reference.eval.incr
    reference.evalRef.assign(result)
    result
  }
}
