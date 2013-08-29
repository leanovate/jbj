/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.calc

import de.leanovate.jbj.core.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.ast.expr.BinaryReferableExpr

case class DivByReferableExpr(reference: ReferableExpr, expr: Expr) extends BinaryReferableExpr {
  override def eval(implicit ctx: Context) = reference.evalRef /= expr.eval
}
