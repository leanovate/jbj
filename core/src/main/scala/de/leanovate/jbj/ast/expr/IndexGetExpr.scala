package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.UndefinedVal

case class IndexGetExpr(expr: Expr, indexExpr: Option[Expr]) extends Expr {
  override def eval(ctx: Context) = {
    val array = expr.eval(ctx)

    array.getAt(indexExpr.map(_.eval(ctx)).getOrElse(UndefinedVal))
  }
}
