package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.ast.{FilePosition, Stmt, Expr}
import de.leanovate.jbj.runtime._
import java.util.concurrent.atomic.AtomicLong
import de.leanovate.jbj.ast.stmt.BlockStmt
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.value.{ValueRef, ArrayVal}
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.BlockContext

case class ForeachValueStmt(position: FilePosition, identifier: String, arrayExpr: Expr, valueName: String, stmts: List[Stmt]) extends Stmt {
  def exec(ctx: Context) = {
    val blockCtx = BlockContext(identifier, ctx)

    arrayExpr.eval(ctx).unref match {
      case array: ArrayVal =>
        execValues(array.keyValues.map(_._2), blockCtx)
      case _ =>
    }
    SuccessExecResult()
  }

  @tailrec
  private def execValues(values: List[Value], context: BlockContext): ExecResult = values match {
    case head :: tail =>
      context.defineVariable(valueName, ValueRef(head))
      execStmts(stmts, context) match {
        case BreakExecResult(depth) if depth > 1 => BreakExecResult(depth - 1)
        case BreakExecResult(_) => SuccessExecResult()
        case result: ReturnExecResult => result
        case _ => execValues(tail, context)
      }
    case Nil => SuccessExecResult()
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

object ForeachValueStmt {
  private val forCount = new AtomicLong()

  def apply(position: FilePosition, arrayExpr: Expr, valueName: String, forBlock: Stmt): ForeachValueStmt = forBlock match {
    case block: BlockStmt => ForeachValueStmt(position, nextIdentifier(), arrayExpr, valueName, block.stmts)
    case stmt => ForeachValueStmt(position, nextIdentifier(), arrayExpr, valueName, stmt :: Nil)
  }

  private def nextIdentifier(): String = "foreachvalue_" + forCount.incrementAndGet()

}
