package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.{Name, Expr}
import de.leanovate.jbj.core.runtime.context.Context

case class ClassConstantExpr(className: Name, name: String) extends Expr {
  override def eval(implicit ctx: Context) = ???
}
