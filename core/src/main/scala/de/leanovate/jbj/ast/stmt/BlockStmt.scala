package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt}
import de.leanovate.jbj.runtime._
import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.BlockContext

case class BlockStmt(position: FilePosition, identifier: String, stmts: List[Stmt]) extends Stmt {
  override def exec(ctx: Context) = {
    execStmts(stmts, ctx)
  }

  @tailrec
  private def execStmts(statements: List[Stmt], context: Context): ExecResult = statements match {
    case head :: tail => head.exec(context) match {
      case SuccessExecResult() => execStmts(tail, context)
      case result => result
    }
    case Nil => SuccessExecResult()
  }
}

object BlockStmt {
  private val blockCount = new AtomicLong()

  def apply(position: FilePosition, stmts: List[Stmt]): BlockStmt = {
    BlockStmt(position, nextIdentifier(), stmts)
  }

  private def nextIdentifier(): String = "block_" + blockCount.incrementAndGet()
}