package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.ast.expr.BinaryReferableExpr

case class AddToReferableExpr(reference: ReferableExpr, expr: Expr) extends BinaryReferableExpr {
  override def eval(implicit ctx: Context) = {
    val result = reference.evalOld.toNum + expr.evalOld.toNum
    reference.evalRef.assign(result)
    result
  }

}
