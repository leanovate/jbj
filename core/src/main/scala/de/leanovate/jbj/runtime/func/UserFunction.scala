package de.leanovate.jbj.runtime.func

import de.leanovate.jbj.ast.{FilePosition, Stmt}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value.{NullVal}
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.ast.stmt.ParameterDef
import de.leanovate.jbj.runtime.context.BlockContext

case class UserFunction(name: String, parameterDefs: List[ParameterDef], stmts: List[Stmt]) extends PFunction {

  def call(ctx: Context, callerPosition: FilePosition, parameters: List[Value]) = {
    val funcCtx = BlockContext(name, ctx)

    parameterDefs.zipWithIndex.foreach {
      case (parameterDef, idx) =>
        val value = parameters.drop(idx).headOption.getOrElse(parameterDef.defaultVal(ctx))
        funcCtx.defineVariable(parameterDef.variableName, ValueRef(value.copy))
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
