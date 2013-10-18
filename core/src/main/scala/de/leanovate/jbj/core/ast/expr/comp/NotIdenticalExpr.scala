/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.comp

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context

case class NotIdenticalExpr(left: Expr, right: Expr) extends Expr {
  override def eval(implicit ctx: Context) = left.eval !== right.eval

  override def phpStr = left.phpStr + "!==" + right.phpStr
}
