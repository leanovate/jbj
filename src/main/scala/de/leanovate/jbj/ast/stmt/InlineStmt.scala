package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.exec.Context

case class InlineStmt(text: String) extends Stmt {
  def exec(ctx: Context) {
    ctx.out.print(text)
  }
}
