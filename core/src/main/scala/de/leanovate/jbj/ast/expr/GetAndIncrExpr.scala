package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.runtime.context.Context

case class GetAndIncrExpr(reference: ReferableExpr) extends Expr {
  def eval(implicit ctx: Context) = reference.evalRef.++
}
