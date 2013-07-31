package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context

case class ModExpr(left: Expr, right: Expr) extends Expr {
  override def eval(ctx: Context) = left.eval(ctx).toInteger % right.eval(ctx).toInteger
}
