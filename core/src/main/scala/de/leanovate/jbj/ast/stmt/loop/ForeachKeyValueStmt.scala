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

  def exec(ctx: Context) = {
    arrayExpr.eval(ctx) match {
      case array: ArrayVal =>
        execValues(array.keyValues.toList, ctx)
      case _ =>
    }
    SuccessExecResult()
  }

  override def initializeStatic(ctx: Context) {
    staticInitializers.foreach(_.initializeStatic(ctx))
  }

  @tailrec
  private def execValues(keyValues: List[(ArrayKey, Value)], context: Context): ExecResult = keyValues match {
    case head :: tail =>
      keyVar.assign(context, head._1.value)
      valueVar.assign(context, head._2)
      execStmts(stmts, context) match {
        case BreakExecResult(depth) if depth > 1 => BreakExecResult(depth - 1)
        case BreakExecResult(_) => SuccessExecResult()
        case result: ReturnExecResult => result
        case _ => execValues(tail, context)
      }
    case Nil => SuccessExecResult()
  }

  @tailrec
  private def execStmts(statements: List[Stmt], context: Context): ExecResult = statements match {
    case head :: tail => head.exec(context) match {
      case SuccessExecResult() => execStmts(tail, context)
      case result => result
    }
    case Nil => SuccessExecResult()
  }
}