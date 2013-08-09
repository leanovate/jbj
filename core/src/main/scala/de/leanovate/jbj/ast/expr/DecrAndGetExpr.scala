package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Reference, Expr}
import de.leanovate.jbj.runtime.Context

case class DecrAndGetExpr(reference: Reference) extends Expr {
  override def eval(implicit ctx: Context) = {
    val result = reference.eval.decr
    reference.assignRef(result)
    result
  }
}
