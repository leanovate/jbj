package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.Stmt
import de.leanovate.jbj.core.runtime.SuccessExecResult
import de.leanovate.jbj.core.runtime.context.Context

case class InlineStmt(text: String) extends Stmt {
  override def exec(implicit ctx: Context) = {
    ctx.out.print(text)
    SuccessExecResult
  }

  override def toXml =
    <InlineStmt line={position.line.toString} file={position.fileName}>
      { text }
    </InlineStmt>
}
