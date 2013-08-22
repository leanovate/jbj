package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.ContinueExecResult
import de.leanovate.jbj.runtime.context.Context

case class ContinueStmt(depth: Option[Expr]) extends Stmt {
  override def exec(implicit ctx: Context) = ContinueExecResult(depth.map(_.eval.asVal.toInteger.asLong).getOrElse(1))
}
