/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.PAny

case class CallByExprRefExpr(callable: Expr, parameters: List[Expr]) extends CallRefExpr {
  override def call(implicit ctx: Context): PAny = {
    callable.eval.call(parameters.map(ExprParam.apply))
  }

  override def phpStr = callable.phpStr + parameters.map(_.phpStr).mkString("(", ", ", ")")
}
