package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

case class CloneExpr(expr: Expr) extends Expr {
  def eval(implicit ctx: Context) = expr.evalOld.copy
}
