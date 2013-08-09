package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.{ContinueExecResult, Context}

case class ContinueStmt(depth: Option[Expr]) extends Stmt {
  override def exec(implicit ctx: Context) = ContinueExecResult(depth.map(_.eval.toInteger.asLong).getOrElse(1))
}
