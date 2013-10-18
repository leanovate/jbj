/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.include

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.runtime.value.{NullVal, BooleanVal}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context

case class RequireExpr(file: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    val filename = file.eval.asVal.toStr.asString
    val currentNamespace = ctx.global.currentNamespace
    val currentAliases = ctx.global.namespaceAliases

    val result = ctx.global.include(filename) match {
      case Some((prog, _)) =>
        prog.exec match {
          case ReturnExecResult(returnExpr) => returnExpr.map(_.byVal).getOrElse(NullVal)
          case _ => BooleanVal.TRUE
        }
      case _ =>
        throw new FatalErrorJbjException("require(): Failed opening required '%s' for inclusion (include_path='%s')".
          format(filename, ctx.currentPosition.fileName))
    }

    ctx.global.currentNamespace = currentNamespace
    ctx.global.namespaceAliases = currentAliases
    result
  }

  override def phpStr = "require " + file.phpStr
}
