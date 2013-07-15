package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.exec.Context

case class EchoStmt(parameters: Seq[Expr]) extends Stmt {
  override def exec(ctx: Context) {
    parameters.foreach {
      expr =>
        expr.eval(ctx).toOutput(ctx.out)
    }
  }
}
