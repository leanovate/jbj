package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{NodeVisitor, Expr, Stmt}
import de.leanovate.jbj.core.runtime.SuccessExecResult
import java.io.PrintStream
import de.leanovate.jbj.core.runtime.context.Context

case class ExprStmt(expr: Expr) extends Stmt {
  override def exec(implicit ctx: Context) = {
    expr.eval
    SuccessExecResult
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    expr.dump(out, ident + "  ")
  }

  override def toXml =
    <ExprStmt line={position.line.toString} file={position.fileName}>
      {expr.toXml}
    </ExprStmt>

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(expr)
}
