package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.value.IntegerVal
import de.leanovate.jbj.core.runtime.context.Context

case class LineNumberConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = IntegerVal(ctx.currentPosition.line)
}
