package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{DoubleVal, NumericVal}
import de.leanovate.jbj.ast.expr.BinaryExpr

case class DivExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(implicit ctx: Context) = left.evalOld.toNum / right.evalOld.toNum
}
