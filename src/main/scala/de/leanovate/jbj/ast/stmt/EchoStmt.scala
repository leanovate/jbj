package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.exec.Context

case class EchoStmt(parameters: List[Expr]) extends Stmt {
  override def exec(ctx: Context) {

  }
}
