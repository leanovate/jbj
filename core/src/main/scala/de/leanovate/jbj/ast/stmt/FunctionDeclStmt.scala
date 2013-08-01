package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodePosition, NamespaceName, StaticInitializer, Stmt}
import de.leanovate.jbj.runtime._
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.context.FunctionContext
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.value.NullVal

case class FunctionDeclStmt(name: NamespaceName, parameterDecls: List[ParameterDecl], stmts: List[Stmt])
  extends Stmt with PFunction {
  private lazy val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  def exec(ctx: Context) = {
    ctx.defineFunction(this)
    SuccessExecResult()
  }

  def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]): Either[Value, ValueRef] = {
    val funcCtx = FunctionContext(name, callerPosition, ctx)

    if (!funcCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(funcCtx))
      funcCtx.static.initialized = true
    }

    parameterDecls.zipWithIndex.foreach {
      case (parameterDef, idx) =>
        val value = parameters.drop(idx).headOption.getOrElse(parameterDef.defaultVal(ctx))
        funcCtx.defineVariable(parameterDef.variableName, ValueRef(value.copy))
    }
    Left(execStmts(stmts, funcCtx))
  }

  @tailrec
  private def execStmts(statements: List[Stmt], context: Context): Value = statements match {
    case head :: tail => head.exec(context) match {
      case ReturnExecResult(returnVal) => returnVal
      case _ => execStmts(tail, context)
    }
    case Nil => NullVal
  }
}
