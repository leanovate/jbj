package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.ast.{FilePosition, Stmt, Expr}
import de.leanovate.jbj.runtime._
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.SuccessExecResult

case class WhileStmt(position: FilePosition, condition: Expr, stmts: List[Stmt]) extends Stmt {
  def exec(ctx: Context): ExecResult = {
    while (condition.eval(ctx).toBool.value) {
      execStmts(stmts, ctx) match {
        case BreakExecResult(depth) if depth > 1 => BreakExecResult(depth - 1)
        case BreakExecResult(_) => return SuccessExecResult()
        case result: ReturnExecResult => return result
        case _ =>
      }
    }
    SuccessExecResult()
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
