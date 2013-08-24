package de.leanovate.jbj.core.ast.stmt.loop

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.core.runtime._
import de.leanovate.jbj.core.runtime.SuccessExecResult
import de.leanovate.jbj.core.ast.stmt.BlockLike
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.BreakExecResult
import de.leanovate.jbj.core.runtime.ReturnExecResult
import de.leanovate.jbj.core.runtime.ContinueExecResult

case class DoWhileStmt(stmts: List[Stmt], condition: Expr) extends Stmt with BlockLike {
  override def exec(implicit ctx: Context): ExecResult = {
    do {
      execStmts(stmts) match {
        case BreakExecResult(depth) if depth > 1 => return BreakExecResult(depth - 1)
        case BreakExecResult(_) => return SuccessExecResult
        case ContinueExecResult(depth) if depth > 1 => return ContinueExecResult(depth - 1)
        case ContinueExecResult(_) =>
        case result: ReturnExecResult => return result
        case _ =>
      }
    } while (condition.eval.asVal.toBool.asBoolean)
    SuccessExecResult
  }

  override def visit[R](visitor: NodeVisitor[R]) =
    visitor(this).thenChildren(stmts).thenChild(condition)
}
