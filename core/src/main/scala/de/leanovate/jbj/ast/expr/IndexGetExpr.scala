package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.value.{ArrayLike, NullVal}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context

case class IndexGetExpr(expr: Expr, indexExpr: Option[Expr]) extends Expr {
  override def eval(implicit ctx: Context) = {
    if (indexExpr.isEmpty)
      throw new FatalErrorJbjException("Cannot use [] for reading")

    expr.evalOld match {
      case array: ArrayLike => array.getAt(indexExpr.get.evalOld).map(_.asVal).getOrElse(NullVal)
      case _ => NullVal
    }
  }
}
