package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{Value, Context}
import de.leanovate.jbj.runtime.value.{ArrayVal, IntegerVal}

case class ArrayCreateExpr(keyValueExprs: List[(Option[Expr], Expr)]) extends Expr {
  def eval(ctx: Context) = {
    ArrayVal(keyValueExprs.map {
      case (keyExpr, valueExpr) =>
        (keyExpr.map(_.eval(ctx)), valueExpr.eval(ctx))
    })
  }
}
