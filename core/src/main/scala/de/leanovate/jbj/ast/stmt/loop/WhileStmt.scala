package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.ast.{FilePosition, Stmt, Expr}
import de.leanovate.jbj.ast.stmt.BlockStmt
import de.leanovate.jbj.runtime._
import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.BlockContext

case class WhileStmt(position: FilePosition, identifier: String, condition: Expr, whileBlock: List[Stmt]) extends Stmt {
  def exec(ctx: Context): ExecResult = {
    val blockCtx = BlockContext(identifier, ctx)

    while (condition.eval(blockCtx).toBool.value) {
      execStmts(whileBlock, blockCtx) match {
        case BreakExecResult(depth) if depth > 1 => BreakExecResult(depth - 1)
        case BreakExecResult(_) => return SuccessExecResult()
        case result: ReturnExecResult => return result
        case _ =>
      }
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

object WhileStmt {
  private val whileCount = new AtomicLong()

  def apply(position: FilePosition, condition: Expr, whileBlock: Stmt): WhileStmt = whileBlock match {
    case block: BlockStmt => WhileStmt(position, nextIdentifier(), condition, block.stmts)
    case stmt => WhileStmt(position, nextIdentifier(), condition, stmt :: Nil)
  }

  private def nextIdentifier(): String = "while_" + whileCount.incrementAndGet()
}