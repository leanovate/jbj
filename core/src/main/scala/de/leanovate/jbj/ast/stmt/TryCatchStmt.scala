package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodeVisitor, Stmt}
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.exception.RuntimeJbjException
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.PVar

case class TryCatchStmt(tryStmts: List[Stmt], catchBlocks: List[CatchBlock], finallyStmts: List[Stmt])
  extends Stmt with BlockLike {

  override def exec(implicit ctx: Context) = try {
    execStmts(tryStmts)
  } catch {
    case e: RuntimeJbjException =>
      catchBlocks.find {
        catchBlock =>
          ctx.global.findClass(catchBlock.exceptionName).exists(e.exception.instanceOf)
      }.map {
        catchBlock =>
          ctx.defineVariable(catchBlock.variableName, PVar(e.exception))
          execStmts(catchBlock.stmts)
      }.getOrElse(SuccessExecResult)
  } finally {
    execStmts(finallyStmts)
  }

  override def visit[R](visitor: NodeVisitor[R]) =
    visitor(this).thenChildren(tryStmts).thenChildren(catchBlocks).thenChildren(finallyStmts)
}
