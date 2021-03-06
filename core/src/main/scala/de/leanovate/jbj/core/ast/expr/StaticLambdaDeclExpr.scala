/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{StaticInitializer, Stmt, Expr}
import de.leanovate.jbj.runtime.context.{StaticMethodContext, MethodContext, FunctionLikeContext, Context}
import de.leanovate.jbj.core.ast.decl.ParameterDecl
import de.leanovate.jbj.core.ast.stmt.FunctionLike
import de.leanovate.jbj.runtime.types.PClosure
import de.leanovate.jbj.runtime.VariableReference
import de.leanovate.jbj.runtime.value.{StringVal, ArrayVal, PVar}

case class StaticLambdaDeclExpr(returnByRef: Boolean, parameterDecls: List[ParameterDecl], lexicalVars: Seq[LexicalVar], stmts: List[Stmt])
  extends Expr with FunctionLike {
  private lazy val staticInitializers = StaticInitializer.collect(stmts: _*)

  override def eval(implicit ctx: Context) = {
    val lexicalValues = if (!lexicalVars.isEmpty) Some(ArrayVal(lexicalVars.map {
      lexicalVar =>
        val varRef = VariableReference(lexicalVar.variableName)
        (Some(StringVal(lexicalVar.variableName)), if (lexicalVar.byRef) {
          varRef.asVar
        } else {
          varRef.asVal.concrete
        })
    }: _*))
    else
      None
    val invoke = {
      funcCtx: FunctionLikeContext =>
        if (!funcCtx.static.initialized) {
          staticInitializers.foreach(_.initializeStatic(funcCtx.static))
          funcCtx.static.initialized = true
        }

        perform(funcCtx, returnByRef, stmts)
    }
    ctx match {
      case MethodContext(instance, _, _) =>
        PClosure(returnByRef, parameterDecls, isStatic = true, Some(instance.pClass), None, lexicalValues, invoke)
      case StaticMethodContext(pMethod, _, _, _) =>
        PClosure(returnByRef, parameterDecls, isStatic = true, Some(pMethod.implementingClass), None, lexicalValues, invoke)
      case _ =>
        PClosure(returnByRef, parameterDecls, isStatic = true, None, None, lexicalValues, invoke)
    }
  }

  override def phpStr = "static function() { ... }"
}
