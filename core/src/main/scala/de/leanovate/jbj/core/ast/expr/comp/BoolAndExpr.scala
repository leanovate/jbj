/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.comp

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.value.BooleanVal
import de.leanovate.jbj.core.ast.expr.{Precedence, BinaryExpr}
import de.leanovate.jbj.runtime.context.Context

case class BoolAndExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(implicit ctx: Context) = {
    if (!left.eval.asVal.toBool.asBoolean)
      BooleanVal.FALSE
    else
      right.eval.asVal.toBool
  }

  override def phpStr = left.phpStr + "&&" + right.phpStr

  override val precedence = Precedence.BoolAnd2
}
