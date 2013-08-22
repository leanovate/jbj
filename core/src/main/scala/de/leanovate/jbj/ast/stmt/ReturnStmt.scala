package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Stmt, Expr}
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.runtime.context.Context

case class ReturnStmt(expr: Option[Expr]) extends Stmt {
  override def exec(implicit ctx: Context) = {
    ReturnExecResult(expr)
  }
}
