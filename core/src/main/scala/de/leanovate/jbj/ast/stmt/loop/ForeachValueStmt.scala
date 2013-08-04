package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.ast.{Reference, StaticInitializer, Stmt, Expr}
import de.leanovate.jbj.runtime._
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.value.ArrayVal
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.SuccessExecResult

case class ForeachValueStmt(arrayExpr: Expr, valueVar:Reference, stmts: List[Stmt])
  extends Stmt with StaticInitializer {

  private val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  def exec(implicit ctx: Context) = {
    arrayExpr.eval match {
      case array: ArrayVal =>
        execValues(array.keyValues.toList.map(_._2))
      case _ =>
    }
    SuccessExecResult
  }

  override def initializeStatic(ctx: Context) {
    staticInitializers.foreach(_.initializeStatic(ctx))
  }

  @tailrec
  private def execValues(values: List[Value])(implicit context: Context): ExecResult = values match {
    case head :: tail =>
      valueVar.assign(head)
      execStmts(stmts) match {
        case BreakExecResult(depth) if depth > 1 => BreakExecResult(depth - 1)
        case BreakExecResult(_) => SuccessExecResult
        case result: ReturnExecResult => result
        case _ => execValues(tail)
      }
    case Nil => SuccessExecResult
  }

  @tailrec
  private def execStmts(statements: List[Stmt])(implicit context: Context): ExecResult = statements match {
    case head :: tail => head.exec match {
      case SuccessExecResult => execStmts(tail)
      case result => result
    }
    case Nil => SuccessExecResult
  }
}
