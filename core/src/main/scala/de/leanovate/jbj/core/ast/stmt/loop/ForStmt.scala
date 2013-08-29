/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.loop

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.core.runtime._
import de.leanovate.jbj.core.runtime.SuccessExecResult
import de.leanovate.jbj.core.ast.stmt.BlockLike
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.BreakExecResult
import de.leanovate.jbj.core.runtime.ReturnExecResult
import de.leanovate.jbj.core.runtime.ContinueExecResult

case class ForStmt(befores: List[Expr], conditions: List[Expr], afters: List[Expr], stmts: List[Stmt])
  extends Stmt with BlockLike {

  override def exec(implicit ctx: Context): ExecResult = {
    befores.foreach(_.eval)
    while (conditions.foldLeft(true) {
      (result, cond) =>
        cond.eval.asVal.toBool.asBoolean
    }) {
      execStmts(stmts) match {
        case BreakExecResult(depth) if depth > 1 => return BreakExecResult(depth - 1)
        case BreakExecResult(_) => return SuccessExecResult
        case ContinueExecResult(depth) if depth > 1 => return ContinueExecResult(depth - 1)
        case ContinueExecResult(_) =>
        case result: ReturnExecResult => return result
        case _ =>
      }
      afters.foreach(_.eval)
    }
    SuccessExecResult
  }

  override def visit[R](visitor: NodeVisitor[R]) =
    visitor(this).thenChildren(befores).thenChildren(conditions).thenChildren(afters).thenChildren(stmts)
}