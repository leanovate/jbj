/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.comp

import de.leanovate.jbj.core.ast.{Name, Expr}
import de.leanovate.jbj.core.runtime.value.{BooleanVal, ObjectVal}
import de.leanovate.jbj.core.runtime.context.Context

case class InstanceOfExpr(expr: Expr, className: Name) extends Expr {
  def eval(implicit ctx: Context) = expr.eval.asVal match {
    case obj: ObjectVal =>
      ctx.global.findClass(className.evalNamespaceName).map {
        pClass => BooleanVal(obj.instanceOf(pClass))
      }.getOrElse(BooleanVal.FALSE)
    case _ => BooleanVal.FALSE
  }
}
