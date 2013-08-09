package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{StaticInitializer, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.exception.RuntimeJbjException
import de.leanovate.jbj.runtime.value.ValueRef
import de.leanovate.jbj.runtime.context.StaticContext

case class TryCatchStmt(tryStmts: List[Stmt], catchBlocks: List[CatchBlock], finallyStmts: List[Stmt])
  extends Stmt with StaticInitializer with BlockLike {

  private val staticInitializers =
    tryStmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer]) ++
      catchBlocks.map(_.stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])).flatten ++
      finallyStmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  override def exec(implicit ctx: Context) = try {
    execStmts(tryStmts)
  } catch {
    case e: RuntimeJbjException =>
      catchBlocks.find {
        catchBlock =>
          ctx.global.findClass(catchBlock.exceptionName).exists(e.exception.instanceOf)
      }.map {
        catchBlock =>
          ctx.defineVariable(catchBlock.variableName, ValueRef(e.exception))
          execStmts(catchBlock.stmts)
      }.getOrElse(SuccessExecResult)
  } finally {
    execStmts(finallyStmts)
  }

  override def initializeStatic(staticCtx: StaticContext)(implicit ctx: Context) {
    staticInitializers.foreach(_.initializeStatic(staticCtx))
  }
}
