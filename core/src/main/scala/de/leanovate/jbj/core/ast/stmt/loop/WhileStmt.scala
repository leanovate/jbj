/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.loop

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.runtime.ContinueExecResult
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.runtime.ContinueExecResult

case class WhileStmt(condition: Expr, stmts: List[Stmt]) extends Stmt with BlockLike {
  override def exec(implicit ctx: Context): ExecResult = {
    while (condition.eval.asVal.toBool.asBoolean) {
      execStmts(stmts) match {
        case BreakExecResult(depth) if depth > 1 => return BreakExecResult(depth - 1)
        case BreakExecResult(_) => return SuccessExecResult
        case ContinueExecResult(depth) if depth > 1 => return ContinueExecResult(depth - 1)
        case ContinueExecResult(_) =>
        case result: ReturnExecResult => return result
        case _ =>
      }
    }
    SuccessExecResult
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(condition).thenChildren(stmts)
}
