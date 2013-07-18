package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.ast.{Stmt, Expr}
import de.leanovate.jbj.ast.stmt.BlockStmt
import de.leanovate.jbj.runtime._
import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.BlockContext

case class WhileStmt(identifier: String, expr: Expr, whileBlock: BlockStmt) extends Stmt {
  def exec(ctx: Context): ExecResult = {
    val blockCtx = BlockContext(identifier, ctx)

    while (expr.eval(blockCtx).toBool.value) {
      execStmts(whileBlock.stmts, blockCtx) match {
        case BreakExecResult() => return SuccessExecResult()
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

  def apply(expr: Expr, whileBlock: BlockStmt): WhileStmt = WhileStmt(nextIdentifier(), expr, whileBlock)

  private def nextIdentifier(): String = "while_" + whileCount.incrementAndGet()
}