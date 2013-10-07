/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.PAny

case class CallByNameRefExpr(functionName: NamespaceName, parameters: List[Expr]) extends CallRefExpr {
  def call(implicit ctx: Context): PAny = {
    ctx.call(functionName.absolutePrefix, parameters.map(ExprParam.apply))
  }
}
