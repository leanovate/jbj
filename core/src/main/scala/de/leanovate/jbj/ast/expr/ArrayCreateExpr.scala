package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{Value, Context}
import de.leanovate.jbj.runtime.value.{ArrayVal, IntegerVal}

case class ArrayCreateExpr(keyValueExprs: List[(Option[Expr], Expr)]) extends Expr {
  def eval(ctx: Context) = {
    var nextIndex: Int = -1

    def handleKey(key: Option[Value]) = {
      key.getOrElse {
        nextIndex += 1
        IntegerVal(nextIndex)
      }
    }

    ArrayVal(keyValueExprs.map {
      case (keyExpr, valueExpr) =>
        (handleKey(keyExpr.map(_.eval(ctx))), valueExpr.eval(ctx))
    })
  }
}
