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
import de.leanovate.jbj.runtime.NamespaceName

case class ConstGetExpr(constName: NamespaceName, relative: Boolean = true) extends Expr {
  override def eval(implicit ctx: Context) = {
    ctx.global.findConstant(constName.toString).getOrElse {
      ctx.log.notice("Use of undefined constant %s - assumed '%s'".format(constName, constName))
      StringVal(constName.toString)
    }
  }
}
