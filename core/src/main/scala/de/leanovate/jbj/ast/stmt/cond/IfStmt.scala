package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{FilePosition, Stmt, Expr}
import de.leanovate.jbj.runtime.{ExecResult, SuccessExecResult, Context}
import scala.annotation.tailrec

case class IfStmt(position: FilePosition, condition: Expr, thenStmts: List[Stmt], elseIfs: List[ElseIfBlock], elseStmts: List[Stmt])
  extends Stmt {

  def exec(ctx: Context) = {
    if (condition.eval(ctx).toBool.value) {
      execStmts(thenStmts, ctx)
    } else {
      elseIfs.find(_.condition.eval(ctx).toBool.value).map {
        elseIf => execStmts(elseIf.themStmts, ctx)
      }.getOrElse {
        execStmts(elseStmts, ctx)
      }
    }
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
