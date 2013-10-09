package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{BlockLike, DeclStmt, Stmt}
import de.leanovate.jbj.runtime.context.Context

case class DeclareDeclStmt(declares: List[Declare], stmts: List[Stmt]) extends DeclStmt with BlockLike {
  override lazy val declStmts = DeclStmt.collect(stmts: _*)

  def register(implicit ctx: Context) = {
    registerDecls
  }

  def exec(implicit ctx: Context) = {
    execStmts(stmts)
  }
}
