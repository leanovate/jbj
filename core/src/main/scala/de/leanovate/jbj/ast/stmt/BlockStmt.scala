package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodeVisitor, Stmt}
import de.leanovate.jbj.runtime.context.Context

case class BlockStmt(stmts: List[Stmt]) extends Stmt with BlockLike {
  override def exec(implicit ctx: Context) = {
    execStmts(stmts)
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(stmts)
}