/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context

case class SilentExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    ctx.log.silent = true
    try {
      expr.eval
    } finally {
      ctx.log.silent = false
    }
  }

  override def phpStr = "@" + expr.phpStr
}
