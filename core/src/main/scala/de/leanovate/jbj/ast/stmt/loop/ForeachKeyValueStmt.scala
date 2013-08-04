package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.ast.{Reference, StaticInitializer, Stmt, Expr}
import de.leanovate.jbj.runtime._
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.value.ArrayVal
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.SuccessExecResult

case class ForeachKeyValueStmt(arrayExpr: Expr, keyVar: Reference, valueVar: Reference,
                               stmts: List[Stmt]) extends Stmt with StaticInitializer {
  private val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  def exec(implicit ctx: Context) = {
    arrayExpr.eval match {
      case array: ArrayVal =>
        execValues(array.keyValues.toList)
      case _ =>
    }
    SuccessExecResult
  }

  override def initializeStatic(implicit ctx: Context) {
    staticInitializers.foreach(_.initializeStatic)
  }

  @tailrec
  private def execValues(keyValues: List[(ArrayKey, Value)])(implicit context: Context): ExecResult = keyValues match {
    case head :: tail =>
      keyVar.assign(head._1.value)
      valueVar.assign(head._2)
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