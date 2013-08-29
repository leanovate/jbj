/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

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
      val result = head.exec
      context.autoRelease()
      result match {
        case SuccessExecResult => execStmts(tail)
        case result => result
      }
    }
    case Nil => SuccessExecResult
  }

}
