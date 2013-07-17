package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{Stmt, Expr}
import de.leanovate.jbj.exec.{SuccessExecResult, Context}
import de.leanovate.jbj.ast.stmt.BlockStmt

case class IfStmt(condition: Expr, thenBlock: BlockStmt, elseIfs: List[ElseIfBlock], elseBlock: Option[BlockStmt])
  extends Stmt {

  def exec(ctx: Context) = {
    if (condition.eval(ctx).toBool.value) {
      thenBlock.exec(ctx)
    } else {
      elseIfs.find(_.condition.eval(ctx).toBool.value).map {
        elseIf => elseIf.thenBlock.exec(ctx)
      }.getOrElse {
        elseBlock.map(_.exec(ctx)).getOrElse(SuccessExecResult())
      }
    }
  }
}
