package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.{ArrayKey, Context}
import de.leanovate.jbj.runtime.value.{ArrayLike, NullVal}

case class IndexGetExpr(expr: Expr, indexExpr: Option[Expr]) extends Expr {
  override def eval(implicit ctx: Context) = {
    val optArrayKey = indexExpr.flatMap {
      expr =>
        ArrayKey(expr.eval)
    }

    optArrayKey.flatMap {
      arrayKey =>
        expr.eval match {
          case array: ArrayLike => array.getAt(arrayKey)
          case _ => None
        }
    }.map(_.value).getOrElse(NullVal)
  }
}
