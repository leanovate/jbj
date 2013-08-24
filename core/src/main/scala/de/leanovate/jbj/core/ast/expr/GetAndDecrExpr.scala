package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.core.runtime.context.Context

case class GetAndDecrExpr(reference: ReferableExpr) extends Expr {
  override def eval(implicit ctx: Context) = reference.evalRef.--
}
