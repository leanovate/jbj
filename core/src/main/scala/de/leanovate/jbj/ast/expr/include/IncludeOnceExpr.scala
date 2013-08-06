package de.leanovate.jbj.ast.expr.include

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context

case class IncludeOnceExpr(file:Expr) extends Expr {
  def eval(implicit ctx: Context) = ???
}
