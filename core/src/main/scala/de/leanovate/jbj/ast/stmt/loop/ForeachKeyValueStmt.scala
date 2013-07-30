package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.ast.{StaticInitializer, NodePosition, Stmt, Expr}
import de.leanovate.jbj.runtime._
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.value.ArrayVal
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.value.ArrayVal.ArrayKey

case class ForeachKeyValueStmt( arrayExpr: Expr, keyName: String, valueName: String,
                               stmts: List[Stmt]) extends Stmt with StaticInitializer {
  private val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  def exec(ctx: Context) = {
    arrayExpr.eval(ctx).unref match {
      case array: ArrayVal =>
        execValues(array.keyValues, ctx)
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
      context.defineVariable(keyName, ValueRef(head._1.value))
      context.defineVariable(valueName, ValueRef(head._2))
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