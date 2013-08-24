package de.leanovate.jbj.core.ast.expr.calc

import de.leanovate.jbj.core.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.ast.expr.BinaryReferableExpr

case class MulByReferableExpr(reference: ReferableExpr, expr: Expr) extends BinaryReferableExpr {
  override def eval(implicit ctx: Context) = reference.evalRef *= expr.eval
}