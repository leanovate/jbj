package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Expr, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}

case class EchoStmt(position: FilePosition, parameters: Seq[Expr]) extends Stmt {
  override def exec(ctx: Context) = {
    parameters.foreach {
      expr =>
        expr.eval(ctx).toOutput(ctx.out)
    }
    SuccessExecResult()
  }
}
