/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.StringVal

case class DirNameConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = {
    val fileName = ctx.currentPosition.fileName
    val idx = fileName.lastIndexOf('/')

    if (idx >= 0)
      StringVal(fileName.substring(0, idx))
    else
      StringVal("")
  }

  override def phpStr = "__DIR__"
}
