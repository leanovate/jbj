package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.{Name, Expr}
import de.leanovate.jbj.runtime.context.Context

case class ClassConstantExpr(className: Name, name: String) extends Expr {
  override def eval(implicit ctx: Context) = ???
}
