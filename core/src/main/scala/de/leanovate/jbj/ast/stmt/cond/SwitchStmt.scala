package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{FilePosition, Expr, Stmt}
import de.leanovate.jbj.runtime._
import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.BlockContext

case class SwitchStmt(position: FilePosition, identifier: String, expr: Expr, cases: List[SwitchCase]) extends Stmt {
  def exec(ctx: Context) = {
    val value = expr.eval(ctx)
    val blockCtx = BlockContext(identifier, ctx)

    execStmts(cases.dropWhile(!_.matches(value, ctx)).map(_.stmts).flatten, blockCtx)
  }

  @tailrec
  private def execStmts(statements: List[Stmt], context: BlockContext): ExecResult = statements match {
    case head :: tail => head.exec(context) match {
      case SuccessExecResult() => execStmts(tail, context)
      case BreakExecResult(depth) if depth > 1 => BreakExecResult(depth - 1)
      case BreakExecResult(_) => SuccessExecResult()
      case result => result
    }
    case Nil => SuccessExecResult()
  }
}

object SwitchStmt {
  private val switchCount = new AtomicLong()

  def apply(position: FilePosition, expr: Expr, cases: List[SwitchCase]): SwitchStmt =
    SwitchStmt(position, nextIdentifier(), expr, cases)

  private def nextIdentifier(): String = "switch_" + switchCount.incrementAndGet()
}