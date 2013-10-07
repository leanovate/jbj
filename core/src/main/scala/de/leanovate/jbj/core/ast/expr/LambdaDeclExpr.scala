/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{StaticInitializer, Stmt, Expr}
import de.leanovate.jbj.runtime.context.{FunctionLikeContext, Context}
import de.leanovate.jbj.core.ast.decl.ParameterDecl
import de.leanovate.jbj.runtime.types.PClosure
import de.leanovate.jbj.core.ast.stmt.FunctionLike
import de.leanovate.jbj.runtime.VariableReference
import de.leanovate.jbj.runtime.value.PVar

case class LambdaDeclExpr(returnByRef: Boolean, parameterDecls: List[ParameterDecl], lexicalVars: Seq[LexicalVar], stmts: List[Stmt])
  extends Expr with FunctionLike {
  private lazy val staticInitializers = StaticInitializer.collect(stmts: _*)

  override def eval(implicit ctx: Context) = {
    PClosure(returnByRef, parameterDecls, lexicalVars.map {
      lexicalVar =>
        val varRef = VariableReference(lexicalVar.variableName)
        (lexicalVar.variableName, if (lexicalVar.byRef) {
          varRef.byVar
        } else {
          PVar(varRef.byVal)
        })
    }, {
      funcCtx: FunctionLikeContext =>
        if (!funcCtx.static.initialized) {
          staticInitializers.foreach(_.initializeStatic(funcCtx.static))
          funcCtx.static.initialized = true
        }

        perform(funcCtx, returnByRef, stmts)
    })
  }
}
