package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.core.runtime.context.Context

case class GetAndIncrExpr(reference: ReferableExpr) extends Expr {
  def eval(implicit ctx: Context) = reference.evalRef.++
}
