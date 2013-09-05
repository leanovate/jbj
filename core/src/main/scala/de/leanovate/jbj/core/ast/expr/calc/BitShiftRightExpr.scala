/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.calc

import de.leanovate.jbj.core.ast.expr.BinaryExpr
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.core.ast.Expr

case class BitShiftRightExpr(left:Expr, right: Expr) extends BinaryExpr {
  override def eval(implicit ctx: Context) = left.eval >> right.eval
}


