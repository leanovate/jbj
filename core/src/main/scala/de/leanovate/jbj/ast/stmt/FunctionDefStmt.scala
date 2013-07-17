package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.exec.{SuccessExecResult, Context}

case class FunctionDefStmt(name: String, body: BlockStmt) extends Stmt {
  def exec(ctx: Context) = SuccessExecResult()
}
