package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{FilePosition, Stmt, Expr}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}

case class IfStmt(position: FilePosition, condition: Expr, thenBlock: Stmt, elseIfs: List[ElseIfBlock], elseBlock: Option[Stmt])
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
