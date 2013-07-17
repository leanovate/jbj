package de.leanovate.jbj.ast.func

import de.leanovate.jbj.ast.{Stmt, Value, Function}
import de.leanovate.jbj.exec.{ReturnExecResult, SuccessExecResult, BlockContext, Context}
import de.leanovate.jbj.ast.value.NullVal
import scala.annotation.tailrec

case class UserFunction(name: String, stmts: List[Stmt]) extends Function {

  def call(ctx: Context, parameters: List[Value]) = {
    val funcCtx = BlockContext(name, ctx)

    execStmts(stmts, funcCtx)
  }

  @tailrec
  private def execStmts(statements: List[Stmt], context: BlockContext): Value = statements match {
    case head :: tail => head.exec(context) match {
      case ReturnExecResult(returnVal) => returnVal
      case _ => execStmts(tail, context)
    }
    case Nil => NullVal
  }


}
