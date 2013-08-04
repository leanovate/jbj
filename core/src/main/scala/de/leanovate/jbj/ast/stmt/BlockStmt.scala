package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{StaticInitializer, Stmt}
import de.leanovate.jbj.runtime._
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.SuccessExecResult

case class BlockStmt(stmts: List[Stmt]) extends Stmt with StaticInitializer with BlockLike {
  private val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  override def exec(implicit ctx: Context) = {
    execStmts(stmts)
  }

  override def initializeStatic(ctx: Context) {
    staticInitializers.foreach(_.initializeStatic(ctx))
  }
}