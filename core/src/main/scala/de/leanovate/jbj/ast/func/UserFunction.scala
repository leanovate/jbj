package de.leanovate.jbj.ast.func

import de.leanovate.jbj.ast.{Stmt, Value, Function}
import de.leanovate.jbj.exec.{ReturnExecResult, SuccessExecResult, BlockContext, Context}
import de.leanovate.jbj.ast.value.{ValueRef, UndefinedVal, NullVal}
import scala.annotation.tailrec
import de.leanovate.jbj.ast.stmt.ParameterDef

case class UserFunction(name: String, parameterDefs: List[ParameterDef], stmts: List[Stmt]) extends Function {

  def call(ctx: Context, parameters: List[Value]) = {
    val funcCtx = BlockContext(name, ctx)

    parameterDefs.zipWithIndex.foreach {
      case (parameterDef, idx) =>
        val value = parameters.drop(idx).headOption.getOrElse(parameterDef.defaultVal(ctx))
        funcCtx.defineVariable(parameterDef.variableName, static = false, ValueRef(value.copy))
    }
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
