package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{NodeVisitor, Stmt}
import de.leanovate.jbj.core.runtime.context.Context

case class BlockStmt(stmts: List[Stmt]) extends Stmt with BlockLike {
  override def exec(implicit ctx: Context) = {
    execStmts(stmts)
  }

  override def toXml() = <block>{stmts.map(_.toXml)}</block>

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(stmts)
}