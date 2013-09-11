/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{RefExpr, Expr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.BooleanVal

case class EmptyExpr(expr: Expr) extends Expr {
  def eval(implicit ctx: Context) = expr match {
    case refExpr: RefExpr =>
      val ref = refExpr.evalRef
      if (!ref.isDefined)
        BooleanVal.TRUE
      else
        !ref.byVal
    case _ =>
      !expr.eval
  }
}
