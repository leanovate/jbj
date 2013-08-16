package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.runtime.value.{PAnyVal, BooleanVal}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.ast.expr.BinaryExpr

case class GeExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(implicit ctx: Context) = BooleanVal(PAnyVal.compare(left.eval, right.eval) >= 0)
}
