/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.calc

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context

case class BitNotExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = ~expr.eval

  override def phpStr = "~" + expr.phpStr
}
