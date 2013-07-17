package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.exec.{BreakExecResult, Context}

object BreakStmt extends Stmt {
  def exec(ctx: Context) = BreakExecResult()
}
