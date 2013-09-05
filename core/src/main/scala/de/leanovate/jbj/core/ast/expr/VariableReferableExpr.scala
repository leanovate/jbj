/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, ReferableExpr}
import de.leanovate.jbj.runtime.value.NullVal
import de.leanovate.jbj.runtime.context.Context

case class VariableReferableExpr(variableName: Name) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    val name = variableName.evalName
    ctx.findVariable(name).getOrElse {
      ctx.log.notice("Undefined variable: %s".format(name))
      NullVal
    }
  }

  override def evalRef(implicit ctx: Context) = ctx.getVariable(variableName.evalName)
}
