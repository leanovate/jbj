package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast.{NodeVisitor, BlockLike, DeclStmt, Stmt}
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.context.Context

case class NamespaceDeclStmt(name: NamespaceName, stmts: List[Stmt]) extends DeclStmt with BlockLike {
  override def register(implicit ctx: Context) {
    val currentNamespace = ctx.global.currentNamespace

    registerDecls

    ctx.global.currentNamespace = currentNamespace
  }

  def exec(implicit ctx: Context) = {
    val currentNamespace = ctx.global.currentNamespace

    val result = execStmts(stmts)

    ctx.global.currentNamespace = currentNamespace

    result
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(stmts)
}
