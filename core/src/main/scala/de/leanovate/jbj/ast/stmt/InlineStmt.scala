package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}

case class InlineStmt(position: FilePosition, text: String) extends Stmt {
  def exec(ctx: Context) = {
    ctx.out.print(text)
    SuccessExecResult()
  }
}
