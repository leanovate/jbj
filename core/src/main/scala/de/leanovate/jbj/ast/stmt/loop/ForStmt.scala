package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.ast._
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.ast.stmt.BlockLike
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.runtime.ContinueExecResult

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