package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Expr, FilePosition, Stmt}
import de.leanovate.jbj.runtime.{BreakExecResult, Context}

case class BreakStmt(position: FilePosition, depth: Option[Expr]) extends Stmt {
  def exec(ctx: Context) = BreakExecResult(depth.map(_.eval(ctx).toNum.toInt).getOrElse(0))
}
