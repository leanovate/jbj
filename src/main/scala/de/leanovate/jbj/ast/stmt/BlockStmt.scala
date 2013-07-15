package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.exec.{SuccessExecResult, Context}

case class BlockStmt(stmts: List[Stmt]) extends Stmt {
  override def exec(ctx: Context) = {
    stmts.foreach(_.exec(ctx))
    SuccessExecResult()
  }
}
