package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.context.Context

case class BreakStmt(depth: Option[Expr]) extends Stmt {
  override def exec(implicit ctx: Context) = BreakExecResult(depth.map(_.evalOld.toInteger.asLong).getOrElse(1))
}
