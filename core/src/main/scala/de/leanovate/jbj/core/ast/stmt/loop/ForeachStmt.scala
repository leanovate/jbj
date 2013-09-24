/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.loop

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.runtime.ContinueExecResult
import de.leanovate.jbj.runtime.types.{PIteratorAggregate, PIterator}

case class ForeachStmt(valueExpr: Expr,
                       keyAssign: Option[ForeachAssignment],
                       valueAssign: ForeachAssignment,
                       stmts: List[Stmt]) extends Stmt with BlockLike {

  def exec(implicit ctx: Context): ExecResult = {
    val (value, isReferenced) = valueExpr match {
      case refExpr: RefExpr =>
        val ref = refExpr.evalRef
        ref.byVal.concrete -> (ref.byVar.refCount > 1)
      case expr =>
        expr.eval.concrete -> false
    }
    if (isReferenced || valueAssign.hasValueRef) {
      value.foreachByVar(execKeyValue).getOrElse(SuccessExecResult)
    } else {
      value.foreachByVal(execKeyValue).getOrElse(SuccessExecResult)
    }
  }

  private def execKeyValue(key: PVal, value: PAny)(implicit ctx: Context): Option[ExecResult] = {
    keyAssign.foreach(_.assignKey(key))
    valueAssign.assignValue(value)
    execStmts(stmts) match {
      case BreakExecResult(depth) if depth > 1 => Some(BreakExecResult(depth - 1))
      case BreakExecResult(_) => Some(SuccessExecResult)
      case ContinueExecResult(depth) if depth > 1 => Some(ContinueExecResult(depth - 1))
      case result: ReturnExecResult => Some(result)
      case _ => None
    }
  }

  override def visit[R](visitor: NodeVisitor[R]) =
    visitor(this).thenChild(valueExpr).thenChild(keyAssign).thenChild(valueAssign).thenChildren(stmts)
}