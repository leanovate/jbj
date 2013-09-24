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
import de.leanovate.jbj.buildins.types.{PIteratorAggregate, PIterator}
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.runtime.ContinueExecResult

case class ForeachStmt(valueExpr: Expr,
                       keyAssign: Option[ForeachAssignment],
                       valueAssign: ForeachAssignment,
                       stmts: List[Stmt]) extends Stmt with BlockLike {

  def exec(implicit ctx: Context) = {
    val (value, isReferenced) = valueExpr match {
      case refExpr: RefExpr =>
        val ref = refExpr.evalRef
        ref.byVal.concrete -> (ref.byVar.refCount > 1)
      case expr =>
        expr.eval.concrete -> false
    }
    value match {
      case array: ArrayVal =>
        array.iteratorReset()
        execValues(array, array.iteratorState.copy(fixedEntries = !isReferenced && !valueAssign.hasValueRef))
      case obj: ObjectVal if obj.instanceOf(PIteratorAggregate) =>
        val iterator = PIteratorAggregate.cast(obj).getIterator()
        iterator.obj.retain()
        iterator.rewind()
        val result = execIterator(iterator)
        iterator.obj.release()
        result
      case obj: ObjectVal if obj.instanceOf(PIterator) =>
        val iterator = PIterator.cast(obj)
        iterator.rewind()
        execIterator(iterator)
      case obj: ObjectVal =>
        obj.iteratorReset()
        execValues(obj, obj.iteratorState.copy(fixedEntries = !isReferenced && !valueAssign.hasValueRef))
      case _ =>
        ctx.log.warn("Invalid argument supplied for foreach()")
        SuccessExecResult
    }
  }

  private def execValues(array: ArrayVal, iteratorState: IteratorState)(implicit context: Context): ExecResult = {
    while (iteratorState.hasNext) {
      keyAssign.foreach(_.assignKey(iteratorState.currentKey))
      val value = if (valueAssign.hasValueRef) {
        iteratorState.currentValue match {
          case pVar: PVar =>
            pVar
          case pVal: PVal =>
            val pVar = PVar(pVal)
            iteratorState.currentValue = pVar
            pVar
        }
      } else {
        iteratorState.currentValue
      }
      valueAssign.assignValue(value)
      iteratorState.advance()
      array.iteratorState = iteratorState.copy(fixedEntries = false)
      execStmts(stmts) match {
        case BreakExecResult(depth) if depth > 1 => return BreakExecResult(depth - 1)
        case BreakExecResult(_) => return SuccessExecResult
        case ContinueExecResult(depth) if depth > 1 => return ContinueExecResult(depth - 1)
        case result: ReturnExecResult => return result
        case _ =>
      }
    }
    SuccessExecResult
  }

  private def execValues(array: ObjectVal, iteratorState: IteratorState)(implicit context: Context): ExecResult = {
    while (iteratorState.hasNext) {
      keyAssign.foreach(_.assignKey(iteratorState.currentKey))
      val value = if (valueAssign.hasValueRef) {
        iteratorState.currentValue match {
          case pVar: PVar =>
            pVar
          case pVal: PVal =>
            val pVar = PVar(pVal)
            iteratorState.currentValue = pVar
            pVar
        }
      } else {
        iteratorState.currentValue
      }
      valueAssign.assignValue(value)
      iteratorState.advance()
      array.iteratorState = iteratorState.copy(fixedEntries = false)
      execStmts(stmts) match {
        case BreakExecResult(depth) if depth > 1 => return BreakExecResult(depth - 1)
        case BreakExecResult(_) => return SuccessExecResult
        case ContinueExecResult(depth) if depth > 1 => return ContinueExecResult(depth - 1)
        case result: ReturnExecResult => return result
        case _ =>
      }
    }
    SuccessExecResult
  }

  private def execIterator(iterator: PIterator)(implicit ctx: Context): ExecResult = {
    while (iterator.valid) {
      valueAssign.assignValue(iterator.current)
      keyAssign.foreach(_.assignKey(iterator.key))
      execStmts(stmts) match {
        case BreakExecResult(depth) if depth > 1 => return BreakExecResult(depth - 1)
        case BreakExecResult(_) => return SuccessExecResult
        case ContinueExecResult(depth) if depth > 1 => return ContinueExecResult(depth - 1)
        case result: ReturnExecResult => return result
        case _ =>
      }
      iterator.next()
    }
    SuccessExecResult
  }

  override def visit[R](visitor: NodeVisitor[R]) =
    visitor(this).thenChild(valueExpr).thenChild(keyAssign).thenChild(valueAssign).thenChildren(stmts)
}