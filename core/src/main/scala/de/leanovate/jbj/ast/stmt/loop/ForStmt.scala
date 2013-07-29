package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.ast.{FilePosition, Expr, Stmt}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.SuccessExecResult
import scala.annotation.tailrec

case class ForStmt(position: FilePosition, befores: List[Expr], conditions: List[Expr], afters: List[Expr], stmts: List[Stmt])
  extends Stmt {

  def exec(ctx: Context): ExecResult = {
    befores.foreach(_.eval(ctx))
    while (conditions.foldLeft(true) {
      (result, cond) =>
        cond.eval(ctx).toBool.value
    }) {
      execStmts(stmts, ctx) match {
        case BreakExecResult(depth) if depth > 1 => BreakExecResult(depth - 1)
        case BreakExecResult(_) => return SuccessExecResult()
        case result: ReturnExecResult => return result
        case _ =>
      }
      afters.foreach(_.eval(ctx))
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