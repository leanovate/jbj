/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.runtime.context.Context

case class FileNameConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = StringVal(ctx.currentPosition.fileName)

  override def phpStr = "__FILE__"
}
