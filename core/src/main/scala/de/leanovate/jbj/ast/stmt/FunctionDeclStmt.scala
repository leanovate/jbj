package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodePosition, NamespaceName, StaticInitializer, Stmt}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.context.FunctionContext
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.value.{ValueOrRef, ValueRef, Value, NullVal}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import java.io.PrintStream

case class FunctionDeclStmt(name: NamespaceName, parameterDecls: List[ParameterDecl], stmts: List[Stmt])
  extends Stmt with PFunction with BlockLike {
  private lazy val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  override def exec(implicit ctx: Context) = {
    ctx.defineFunction(this)
    SuccessExecResult
  }

  override def call(ctx: Context, callerPosition: NodePosition, parameters: List[ValueOrRef]) = {
    implicit val funcCtx = FunctionContext(name, callerPosition, ctx)

    if (!funcCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(funcCtx.static))
      funcCtx.static.initialized = true
    }

    parameterDecls.zipWithIndex.foreach {
      case (parameterDef, idx) =>
        val value = parameters.drop(idx).headOption.getOrElse(parameterDef.defaultVal(ctx))
        funcCtx.defineVariable(parameterDef.variableName, ValueRef(value.value.copy))
    }
    execStmts(stmts) match {
      case SuccessExecResult => NullVal
      case ReturnExecResult(retVal) => retVal
      case result: BreakExecResult =>
        throw new FatalErrorJbjException("Cannot break/continue %d level".format(result.depth))(ctx, result.position)
      case result: ContinueExecResult =>
        throw new FatalErrorJbjException("Cannot break/continue %d level".format(result.depth))(ctx, result.position)
    }
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + name.toString + parameterDecls.map(_.variableName).mkString(" (", ", ", ") ") + position)
    stmts.foreach {
      stmt =>
        stmt.dump(out, ident + "  ")
    }
  }
}
