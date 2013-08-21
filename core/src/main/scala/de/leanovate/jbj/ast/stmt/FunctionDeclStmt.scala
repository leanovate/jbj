package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast._
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.value.{PVar, NullVal}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.{Context, FunctionContext}
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.runtime.ContinueExecResult

case class FunctionDeclStmt(name: NamespaceName, returnByRef: Boolean, parameterDecls: List[ParameterDecl], stmts: List[Stmt])
  extends Stmt with PFunction with BlockLike with FunctionLike {
  private lazy val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  override def exec(implicit ctx: Context) = {
    ctx.defineFunction(this)
    SuccessExecResult
  }

  override def call(parameters: List[Expr])(implicit callerCtx: Context) = {
    implicit val funcCtx = FunctionContext(name, callerCtx)

    if (!funcCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(funcCtx.static))
      funcCtx.static.initialized = true
    }

    setParameters(funcCtx, callerCtx, parameters)
    perform(funcCtx, returnByRef, stmts)
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + name.toString + parameterDecls.map(_.variableName).mkString(" (", ", ", ") ") + position)
    stmts.foreach {
      stmt =>
        stmt.dump(out, ident + "  ")
    }
  }
}
