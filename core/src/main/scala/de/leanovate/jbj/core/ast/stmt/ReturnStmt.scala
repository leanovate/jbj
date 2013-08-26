package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{Stmt, Expr}
import de.leanovate.jbj.core.runtime.ReturnExecResult
import de.leanovate.jbj.core.runtime.context.{FunctionContext, Context}

case class ReturnStmt(expr: Option[Expr]) extends Stmt {
  override def exec(implicit ctx: Context) = {
    ReturnExecResult(expr)
  }
}
