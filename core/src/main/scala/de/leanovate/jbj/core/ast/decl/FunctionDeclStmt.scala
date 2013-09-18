/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.{Context, FunctionContext}
import de.leanovate.jbj.core.ast.stmt.FunctionLike
import de.leanovate.jbj.runtime.types.{PParam, PFunction}

case class FunctionDeclStmt(name: NamespaceName, returnByRef: Boolean, parameterDecls: List[ParameterDecl], stmts: List[Stmt])
  extends DeclStmt with PFunction with BlockLike with FunctionLike {
  private lazy val staticInitializers = StaticInitializer.collect(stmts: _*)
  var _registered = false

  override def parameters = parameterDecls.toSeq

  override def exec(implicit ctx: Context) = {
    if (!_registered) {
      parameterDecls.foreach(_.check)
      ctx.defineFunction(this)
    }
    SuccessExecResult
  }

  override def register(implicit ctx: Context) {
    parameterDecls.foreach(_.check)
    ctx.defineFunction(this)
    _registered = true
  }

  override def call(parameters: List[PParam])(implicit callerCtx: Context) = {
    implicit val funcCtx = FunctionContext(name, callerCtx)

    funcCtx.currentPosition = position

    if (!funcCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(funcCtx.static))
      funcCtx.static.initialized = true
    }

    setParameters(funcCtx, callerCtx, parameters)
    perform(funcCtx, returnByRef, stmts)
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(parameterDecls).thenChildren(stmts)
}
