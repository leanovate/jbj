/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.calc

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.ast.expr.{Precedence, BinaryExpr}
import de.leanovate.jbj.runtime.context.Context

case class ModExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(implicit ctx: Context) = left.eval % right.eval

  override def phpStr = left.phpStr + "%" + right.phpStr

  override val precedence = Precedence.MulDiv
}
