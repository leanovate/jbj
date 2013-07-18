package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime._
import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.BlockContext

case class SwitchStmt(identifier: String, expr: Expr, cases: List[SwitchCase]) extends Stmt {
  def exec(ctx: Context) = {
    val value = expr.eval(ctx)
    val blockCtx = BlockContext(identifier, ctx)

    execStmts(cases.dropWhile(!_.matches(value, ctx)).map(_.stmts).flatten, blockCtx)
  }

  @tailrec
  private def execStmts(statements: List[Stmt], context: BlockContext): ExecResult = statements match {
    case head :: tail => head.exec(context) match {
      case SuccessExecResult() => execStmts(tail, context)
      case BreakExecResult() => SuccessExecResult()
      case result => result
    }
    case Nil => SuccessExecResult()
  }
}

object SwitchStmt {
  private val switchCount = new AtomicLong()

  def apply(expr: Expr, cases: List[SwitchCase]): SwitchStmt = SwitchStmt(nextIdentifier(), expr, cases)

  private def nextIdentifier(): String = "switch_" + switchCount.incrementAndGet()
}