package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.runtime.context.Context

case class FileNameConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = StringVal(ctx.currentPosition.fileName)
}
