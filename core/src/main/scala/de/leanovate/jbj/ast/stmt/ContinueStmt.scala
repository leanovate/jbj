package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.exec.{ContinueExecResult, Context}

object ContinueStmt extends Stmt {
  def exec(ctx: Context) = ContinueExecResult()
}
