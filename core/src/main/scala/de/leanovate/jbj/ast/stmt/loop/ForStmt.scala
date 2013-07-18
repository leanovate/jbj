package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime._
import java.util.concurrent.atomic.AtomicLong
import de.leanovate.jbj.ast.stmt.BlockStmt
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.BlockContext
import scala.annotation.tailrec

case class ForStmt(identifier: String, beforeStmt: Stmt, condition: Expr, afterStmt: Stmt, forBlock: BlockStmt)
  extends Stmt {

  def exec(ctx: Context): ExecResult = {
    val blockCtx = BlockContext(identifier, ctx)

    beforeStmt.exec(blockCtx)
    while (condition.eval(blockCtx).toBool.value) {
      execStmts(forBlock.stmts, blockCtx) match {
        case BreakExecResult() => return SuccessExecResult()
        case result: ReturnExecResult => return result
        case _ =>
      }
      afterStmt.exec(blockCtx)
    }
    SuccessExecResult()
  }

  @tailrec
  private def execStmts(statements: List[Stmt], context: BlockContext): ExecResult = statements match {
    case head :: tail => head.exec(context) match {
      case SuccessExecResult() => execStmts(tail, context)
      case result => result
    }
    case Nil => SuccessExecResult()
  }

}

object ForStmt {
  private val forCount = new AtomicLong()

  def apply(beforeStmt: Stmt, condition: Expr, afterStmt: Stmt, forBlock: BlockStmt): ForStmt =
    ForStmt(nextIdentifier(), beforeStmt, condition, afterStmt, forBlock)

  private def nextIdentifier(): String = "for_" + forCount.incrementAndGet()
}