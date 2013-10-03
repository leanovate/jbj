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

case class StaticLambdaDeclExpr(returnByRef: Boolean, parameterDecls: List[ParameterDecl], stmts: List[Stmt]) extends Expr {
  def eval(implicit ctx: Context) = ???
}
