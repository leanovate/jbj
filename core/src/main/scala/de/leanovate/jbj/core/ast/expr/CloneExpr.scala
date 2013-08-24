package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

case class CloneExpr(expr: Expr) extends Expr {
  def eval(implicit ctx: Context) = expr.eval.asVal.clone
}
