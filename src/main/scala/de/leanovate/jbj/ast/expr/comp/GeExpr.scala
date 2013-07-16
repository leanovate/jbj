package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.value.BooleanVal
import de.leanovate.jbj.exec.Context
import de.leanovate.jbj.ast.Expr

case class GeExpr(left: Expr, right: Expr) extends AbstractCompExpr {
  def eval(ctx: Context) = BooleanVal(comp(ctx) >= 0)
}
