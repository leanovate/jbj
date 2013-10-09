package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast.{NodeVisitor, BlockLike, DeclStmt, Stmt}
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class NamespaceDeclStmt(name: NamespaceName, stmts: List[Stmt]) extends DeclStmt with BlockLike {
  override lazy val declStmts = DeclStmt.collect(stmts: _*)

  override def register(implicit ctx: Context) {
    if (ctx.global.namespaceExclusive)
      throw new FatalErrorJbjException("Namespace declarations cannot be nested")
    else if (!ctx.global.currentNamespace.path.isEmpty)
      throw new FatalErrorJbjException("Cannot mix bracketed namespace declarations with unbracketed namespace declarations")

    val currentNamespace = ctx.global.currentNamespace
    val currentAliases = ctx.global.namespaceAliases

    ctx.global.resetCurrentNamepsace()
    ctx.global.namespaceExclusive = true
    ctx.global.currentNamespace = name.absolute

    registerDecls

    ctx.global.currentNamespace = currentNamespace
    ctx.global.namespaceAliases = currentAliases
    ctx.global.namespaceExclusive = false
  }

  def exec(implicit ctx: Context) = {
    if (ctx.global.namespaceExclusive)
      throw new FatalErrorJbjException("Namespace declarations cannot be nested")
    else if (!ctx.global.currentNamespace.path.isEmpty)
      throw new FatalErrorJbjException("Cannot mix bracketed namespace declarations with unbracketed namespace declarations")

    val currentNamespace = ctx.global.currentNamespace
    val currentAliases = ctx.global.namespaceAliases

    ctx.global.resetCurrentNamepsace()
    ctx.global.namespaceExclusive = true
    ctx.global.currentNamespace = name.absolute

    val result = execStmts(stmts)

    ctx.global.currentNamespace = currentNamespace
    ctx.global.namespaceAliases = currentAliases
    ctx.global.namespaceExclusive = false

    result
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(stmts)
}
