package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.Context

case class ThrowStmt(expr: Expr) extends Stmt {
  override def exec(implicit ctx: Context) = ???
}
