package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.Reference.--

case class DecrAndGetExpr(reference: ReferableExpr) extends Expr {
  override def eval(implicit ctx: Context) = --(reference.evalRef)
}
