package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodePosition, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}

case class InlineStmt(text: String) extends Stmt {
  def exec(ctx: Context) = {
    ctx.out.print(text)
    SuccessExecResult()
  }
}
