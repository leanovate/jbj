package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodeVisitor, Expr, Stmt}
import de.leanovate.jbj.runtime.SuccessExecResult
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.Context

case class EchoStmt(parameters: Seq[Expr]) extends Stmt {
  override def exec(implicit ctx: Context) = {
    parameters.foreach {
      expr =>
        ctx.out.print(expr.evalOld.toOutput)
    }
    SuccessExecResult
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    parameters.foreach {
      parameter =>
        parameter.dump(out, ident + "  ")
    }
  }

  override def toXml =
    <EchoStmt line={position.line.toString} file={position.fileName}>
      { parameters.map {parameter => <parameter> { parameter.toXml } </parameter>} }
    </EchoStmt>

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(parameters)
}
