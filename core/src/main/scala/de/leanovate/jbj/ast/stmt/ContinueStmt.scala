package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{ContinueExecResult, Context}

case class ContinueStmt(depth: Int) extends Stmt {
  def exec(ctx: Context) = ContinueExecResult(depth)
}
