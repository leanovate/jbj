package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt}
import de.leanovate.jbj.runtime.{ContinueExecResult, Context}

case class ContinueStmt(position:FilePosition,depth: Int) extends Stmt {
  def exec(ctx: Context) = ContinueExecResult(depth)
}
