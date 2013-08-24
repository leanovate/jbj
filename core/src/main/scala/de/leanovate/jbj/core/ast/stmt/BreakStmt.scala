package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{Expr, Stmt}
import de.leanovate.jbj.core.runtime.BreakExecResult
import de.leanovate.jbj.core.runtime.context.Context

case class BreakStmt(depth: Option[Expr]) extends Stmt {
  override def exec(implicit ctx: Context) = BreakExecResult(depth.map(_.eval.asVal.toInteger.asLong).getOrElse(1))
}
