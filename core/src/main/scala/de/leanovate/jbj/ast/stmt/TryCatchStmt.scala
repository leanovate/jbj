package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{StaticInitializer, Stmt}
import de.leanovate.jbj.runtime.Context

case class TryCatchStmt(tryStmts: List[Stmt], catchBlocks: List[CatchBlock], finallyStmts: List[Stmt]) extends Stmt with StaticInitializer {
  private val staticInitializers =
    tryStmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer]) ++
      catchBlocks.map(_.stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])).flatten ++
      finallyStmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  override def exec(implicit ctx: Context) = ???

  override def initializeStatic(implicit ctx: Context) {
    staticInitializers.foreach(_.initializeStatic)
  }
}
