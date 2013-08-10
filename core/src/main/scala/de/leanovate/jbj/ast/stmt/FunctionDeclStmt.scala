package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast._
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.value.NullVal
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.FunctionContext
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.runtime.ContinueExecResult

case class FunctionDeclStmt(name: NamespaceName, parameterDecls: List[ParameterDecl], stmts: List[Stmt])
  extends Stmt with PFunction with BlockLike with FunctionLike {
  private lazy val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  override def exec(implicit ctx: Context) = {
    ctx.defineFunction(this)
    SuccessExecResult
  }

  override def call(ctx: Context, callerPosition: NodePosition, parameters: List[Expr]) = {
    implicit val funcCtx = FunctionContext(name, callerPosition, ctx)

    if (!funcCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(funcCtx.static))
      funcCtx.static.initialized = true
    }

    setParameters(funcCtx, ctx, callerPosition, parameters)
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
