package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.runtime.context.Context

case class FileNameConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = StringVal(ctx.currentPosition.fileName)
}
