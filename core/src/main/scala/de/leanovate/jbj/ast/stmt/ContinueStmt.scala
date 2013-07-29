package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Expr, FilePosition, Stmt}
import de.leanovate.jbj.runtime.{ContinueExecResult, Context}

case class ContinueStmt(position: FilePosition, depth: Option[Expr]) extends Stmt {
  def exec(ctx: Context) = ContinueExecResult(depth.map(_.eval(ctx).toNum.toInt).getOrElse(1))
}
