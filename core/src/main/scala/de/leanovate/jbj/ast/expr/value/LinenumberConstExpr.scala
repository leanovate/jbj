package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.runtime.context.Context

case class LineNumberConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = IntegerVal(ctx.currentPosition.line)
}
