package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.Reference.--

case class DecrAndGetExpr(reference: ReferableExpr) extends Expr {
  override def eval(implicit ctx: Context) = --(reference.evalRef)
}
