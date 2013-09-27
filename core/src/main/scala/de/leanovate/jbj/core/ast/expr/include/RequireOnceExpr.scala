/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.include

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.value.BooleanVal
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context

case class RequireOnceExpr(file: Expr) extends Expr {
  def eval(implicit ctx: Context) = {
    val filename = file.eval.asVal.toStr.asString
    val currentNamespace = ctx.global.currentNamespace
    val currentAliases = ctx.global.namespaceAliases

    val result = ctx.global.include(filename) match {
      case Some((prog, true)) =>
        prog.exec
        BooleanVal.TRUE
      case Some((_, false)) =>
        BooleanVal.FALSE
      case _ =>
        throw new FatalErrorJbjException("require(): Failed opening required '%s' for inclusion (include_path='%s')".
          format(filename, ctx.currentPosition.fileName))
    }

    ctx.global.currentNamespace = currentNamespace
    ctx.global.namespaceAliases = currentAliases
    result
  }
}
