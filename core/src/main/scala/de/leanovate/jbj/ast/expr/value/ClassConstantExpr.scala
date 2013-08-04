package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.{Expr, NamespaceName}
import de.leanovate.jbj.runtime.Context

case class ClassConstantExpr(className: NamespaceName, name: String) extends Expr {
  override def eval(implicit ctx: Context) = ???
}
