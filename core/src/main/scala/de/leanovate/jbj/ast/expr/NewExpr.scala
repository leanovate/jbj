package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{NamespaceName, Expr}
import de.leanovate.jbj.runtime.Context

case class NewExpr(className: NamespaceName) extends Expr {
  def eval(ctx: Context) = ???
}
