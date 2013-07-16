package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{Stmt, Expr}
import de.leanovate.jbj.exec.{SuccessExecResult, Context}
import de.leanovate.jbj.ast.stmt.BlockStmt

case class IfStmt(condition: Expr, then: BlockStmt) extends Stmt {
  def exec(ctx: Context) = {
    if (condition.eval(ctx).toBool.value) {
      then.exec(ctx)
    } else {
      SuccessExecResult()
    }
  }
}
