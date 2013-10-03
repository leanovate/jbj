/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Stmt, Expr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.core.ast.decl.ParameterDecl
import de.leanovate.jbj.runtime.types.PClosure
import de.leanovate.jbj.core.ast.stmt.FunctionLike
import de.leanovate.jbj.runtime.value.NullVal

case class LambdaDeclExpr(returnByRef: Boolean, parameterDecls: List[ParameterDecl], stmts: List[Stmt]) extends Expr with FunctionLike {
  def eval(implicit ctx: Context) = {
    PClosure(returnByRef, parameterDecls, {
      (params, callerContext) =>
        NullVal
    })
  }
}
