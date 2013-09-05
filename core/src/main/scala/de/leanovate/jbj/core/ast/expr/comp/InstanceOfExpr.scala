/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.comp

import de.leanovate.jbj.core.ast.{Name, Expr}
import de.leanovate.jbj.runtime.value.{BooleanVal, ObjectVal}
import de.leanovate.jbj.runtime.context.Context

case class InstanceOfExpr(expr: Expr, className: Name) extends Expr {
  def eval(implicit ctx: Context) = expr.eval.asVal match {
    case obj: ObjectVal =>
      ctx.global.findInterfaceOrClass(className.evalNamespaceName, autoload = false).map {
        case Right(pClass) => BooleanVal(obj.instanceOf(pClass))
        case Left(pInterface) => BooleanVal(obj.instanceOf(pInterface))
      }.getOrElse(BooleanVal.FALSE)
    case _ => BooleanVal.FALSE
  }
}
