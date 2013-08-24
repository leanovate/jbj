package de.leanovate.jbj.core.ast.stmt

import scala.annotation.tailrec
import de.leanovate.jbj.core.ast.Stmt
import de.leanovate.jbj.core.runtime.{ExecResult, SuccessExecResult}
import de.leanovate.jbj.core.runtime.context.Context

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
