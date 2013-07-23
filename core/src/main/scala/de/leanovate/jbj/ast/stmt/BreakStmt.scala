package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{BreakExecResult, Context}

case class BreakStmt(depth:Int) extends Stmt {
  def exec(ctx: Context) = BreakExecResult(depth)
}
