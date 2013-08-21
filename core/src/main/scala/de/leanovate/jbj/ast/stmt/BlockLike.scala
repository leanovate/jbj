package de.leanovate.jbj.ast.stmt

import scala.annotation.tailrec
import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{ExecResult, SuccessExecResult}
import de.leanovate.jbj.runtime.context.Context

trait BlockLike {
  @tailrec
  final def execStmts(statements: List[Stmt])(implicit context: Context): ExecResult = statements match {
    case head :: tail => {
      context.currentPosition = head.position
      head.exec match {
        case SuccessExecResult => execStmts(tail)
        case result => result
      }
    }
    case Nil => SuccessExecResult
  }

}
