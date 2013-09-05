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

    ctx.global.include(filename) match {
      case Some((prog, _)) =>
        prog.exec match {
          case ReturnExecResult(returnExpr) => returnExpr.map(_.eval.asVal).getOrElse(NullVal)
          case _ => BooleanVal.TRUE
        }
      case _ =>
        ctx.log.warn("include(): Failed opening '%s' for inclusion (include_path='%s')".format(filename, ctx.currentPosition.fileName))
        BooleanVal.FALSE
    }
  }
}
