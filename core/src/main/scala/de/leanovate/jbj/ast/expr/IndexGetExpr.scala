package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{ArrayKey, Context}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class IndexGetExpr(expr: Expr, indexExpr: Option[Expr]) extends Expr {
  override def eval(implicit ctx: Context) = {
    val optArrayKey = indexExpr.flatMap {
      expr =>
        ArrayKey(expr.eval)
    }

    optArrayKey.map {
      arrayKey =>
        val array = expr.eval
        array.getAt(arrayKey)

    }.getOrElse(UndefinedVal)
  }
}
