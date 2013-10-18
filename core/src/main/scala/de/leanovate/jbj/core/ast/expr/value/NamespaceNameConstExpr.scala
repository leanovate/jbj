package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.StringVal

case class NamespaceNameConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = StringVal(ctx.global.currentNamespace.toString)

  override def phpStr = "__NAMESPACE__"
}
