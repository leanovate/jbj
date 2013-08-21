package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.SuccessExecResult
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.Context

case class ExprStmt(expr: Expr) extends Stmt {
  override def exec(implicit ctx: Context) = {
    expr.evalOld
    SuccessExecResult
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    expr.dump(out, ident + "  ")
  }

  override def toXml =
    <ExprStmt line={position.line.toString} file={position.fileName}>
      { expr.toXml }
    </ExprStmt>
}
