package de.leanovate.jbj.ast.func

import de.leanovate.jbj.ast.{Stmt, Value, Function}
import de.leanovate.jbj.exec.{ReturnExecResult, SuccessExecResult, FunctionContext, Context}
import de.leanovate.jbj.ast.value.NullVal
import scala.annotation.tailrec

case class UserFunction(name: String, stmts: List[Stmt]) extends Function {

  def call(ctx: Context, parameters: List[Value]) = {
    val funcCtx = FunctionContext(ctx)

    execStmts(stmts, funcCtx)
  }

  @tailrec
  private def execStmts(statements: List[Stmt], context: FunctionContext): Value = statements match {
    case head :: tail => head.exec(context) match {
      case SuccessExecResult() => execStmts(tail, context)
      case ReturnExecResult(returnVal) => returnVal
    }
    case Nil => NullVal
  }


}
