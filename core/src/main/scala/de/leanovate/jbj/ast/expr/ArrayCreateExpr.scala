package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.ArrayVal

case class ArrayCreateExpr(keyValueExprs: List[(Option[Expr], Expr)]) extends Expr {
  override def eval(implicit ctx: Context) = {
    ArrayVal(keyValueExprs.map {
      case (keyExpr, valueExpr) =>
        (keyExpr.map(_.eval), valueExpr.eval)
    }: _*)
  }
}
