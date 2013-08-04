package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}

case class InlineStmt(text: String) extends Stmt {
  override def exec(implicit ctx: Context) = {
    ctx.out.print(text)
    SuccessExecResult
  }
}
