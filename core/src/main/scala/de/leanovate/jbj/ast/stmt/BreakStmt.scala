package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt}
import de.leanovate.jbj.runtime.{BreakExecResult, Context}

case class BreakStmt(position: FilePosition, depth: Long) extends Stmt {
  def exec(ctx: Context) = BreakExecResult(depth)
}
