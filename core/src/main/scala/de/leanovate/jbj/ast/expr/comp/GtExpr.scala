package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.{PVal, BooleanVal}
import de.leanovate.jbj.ast.expr.BinaryExpr

case class GtExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(implicit ctx: Context) = BooleanVal(PVal.compare(left.eval, right.eval) > 0)
}
