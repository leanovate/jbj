package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Value, Stmt}
import de.leanovate.jbj.exec._
import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec
import de.leanovate.jbj.ast.value.NullVal
import de.leanovate.jbj.exec.SuccessExecResult
import de.leanovate.jbj.exec.BlockContext

case class BlockStmt(identifier: String, stmts: List[Stmt]) extends Stmt {
  override def exec(ctx: Context) = {
    val blockCtx = BlockContext(identifier, ctx)
    execStmts(stmts, blockCtx)
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

object BlockStmt {
  private val blockCount = new AtomicLong()

  def apply(stmts: List[Stmt]): BlockStmt = {
    BlockStmt(nextIdentifier(), stmts)
  }

  private def nextIdentifier(): String = "block_" + blockCount.incrementAndGet()
}