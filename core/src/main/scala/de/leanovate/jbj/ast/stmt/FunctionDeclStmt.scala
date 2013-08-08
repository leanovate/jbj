package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodePosition, NamespaceName, StaticInitializer, Stmt}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.context.FunctionContext
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.value.{Value, NullVal}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class FunctionDeclStmt(name: NamespaceName, parameterDecls: List[ParameterDecl], stmts: List[Stmt])
  extends Stmt with PFunction with BlockLike {
  private lazy val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  override def exec(implicit ctx: Context) = {
    ctx.defineFunction(this)
    SuccessExecResult
  }

  def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]): Either[Value, ValueRef] = {
    implicit val funcCtx = FunctionContext(name, callerPosition, ctx)

    if (!funcCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(funcCtx))
      funcCtx.static.initialized = true
    }

    parameterDecls.zipWithIndex.foreach {
      case (parameterDef, idx) =>
        val value = parameters.drop(idx).headOption.getOrElse(parameterDef.defaultVal(ctx))
        funcCtx.defineVariable(parameterDef.variableName, ValueRef(value.copy))
    }
    execStmts(stmts) match {
      case SuccessExecResult => Left(NullVal)
      case ReturnExecResult(retVal) => Left(retVal)
      case result: BreakExecResult =>
        throw new FatalErrorJbjException("Cannot break/continue %d level".format(result.depth))(ctx, result.position)
      case result: ContinueExecResult =>
        throw new FatalErrorJbjException("Cannot break/continue %d level".format(result.depth))(ctx, result.position)
    }
  }
}
