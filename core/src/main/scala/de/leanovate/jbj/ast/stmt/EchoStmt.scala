package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import java.io.PrintStream

case class EchoStmt(parameters: Seq[Expr]) extends Stmt {
  override def exec(implicit ctx: Context) = {
    parameters.foreach {
      expr =>
        expr.eval.toOutput(ctx.out)
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
}
