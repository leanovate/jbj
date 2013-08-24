package de.leanovate.jbj.core.ast.expr.comp

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.value.BooleanVal
import de.leanovate.jbj.core.ast.expr.BinaryExpr
import de.leanovate.jbj.core.runtime.context.Context

case class BoolAndExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(implicit ctx: Context) = {
    if (!left.eval.asVal.toBool.asBoolean)
      BooleanVal.FALSE
    else
      right.eval.asVal.toBool
  }
}
