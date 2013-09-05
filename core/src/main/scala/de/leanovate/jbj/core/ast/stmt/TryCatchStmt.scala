/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{BlockLike, NodeVisitor, Stmt}
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
          ctx.global.findClass(catchBlock.exceptionName, autoload = false).exists(e.exception.instanceOf)
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
