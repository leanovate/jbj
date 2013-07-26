package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.StringVal

case class DotExpr(left: Expr, right: Expr) extends Expr {
  def position = left.position

  def eval(ctx: Context) = StringVal(left.eval(ctx).toStr.value + right.eval(ctx).toStr.value)
}
