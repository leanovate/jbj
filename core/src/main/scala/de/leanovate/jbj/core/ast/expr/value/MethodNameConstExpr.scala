/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.runtime.context.{ClassContext, StaticMethodContext, Context, MethodContext}
import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.ast.Expr

case class MethodNameConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = ctx match {
    case ClassContext(pClass, _, _) => StringVal(pClass.name.toString)
    case MethodContext(inst, pMethod, _) => StringVal(pMethod.declaringClass.name.toString + "::" + pMethod.name)
    case StaticMethodContext(pMethod, _) => StringVal(pMethod.declaringClass.name.toString + "::" + pMethod.name)
    case _ => StringVal("")
  }

}
