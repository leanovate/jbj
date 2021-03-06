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
import de.leanovate.jbj.runtime.context.Context

case class IncludeExpr(file: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    val filename = file.eval.asVal.toStr.asString
    val currentNamespace = ctx.global.currentNamespace
    val currentAliases = ctx.global.namespaceAliases

    val result =    ctx.global.include(filename) match {
      case Some((script, _)) =>
        script.exec match {
          case ReturnExecResult(returnExpr) => returnExpr.map(_.byVal).getOrElse(NullVal)
          case _ => BooleanVal.TRUE
        }
      case _ =>
        ctx.log.warn("include(): Failed opening '%s' for inclusion (include_path='%s')".format(filename, ctx.currentPosition.fileName))
        BooleanVal.FALSE
    }

    ctx.global.currentNamespace = currentNamespace
    ctx.global.namespaceAliases = currentAliases
    result
  }

  override def phpStr = "include " + file.phpStr
}
