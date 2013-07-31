package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.StringVal

case class ConcatExpr(left: Expr, right: Expr) extends Expr {
  override def eval(ctx: Context) = left.eval(ctx).toStr dot right.eval(ctx).toStr
}
