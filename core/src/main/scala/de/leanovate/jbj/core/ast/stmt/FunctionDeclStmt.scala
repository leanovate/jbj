/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.core.runtime._
import de.leanovate.jbj.core.runtime.SuccessExecResult
import de.leanovate.jbj.core.runtime.context.{Context, FunctionContext}

case class FunctionDeclStmt(name: NamespaceName, returnByRef: Boolean, parameterDecls: List[ParameterDecl], stmts: List[Stmt])
  extends Stmt with PFunction with BlockLike with FunctionLike {
  private lazy val staticInitializers = StaticInitializer.collect(stmts:_*)

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

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(parameterDecls).thenChildren(stmts)
}
