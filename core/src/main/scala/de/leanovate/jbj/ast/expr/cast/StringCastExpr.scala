package de.leanovate.jbj.ast.expr.cast

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

case class StringCastExpr(expr:Expr) extends Expr{
  def eval(implicit ctx: Context) = expr.eval.asVal.toStr
}
