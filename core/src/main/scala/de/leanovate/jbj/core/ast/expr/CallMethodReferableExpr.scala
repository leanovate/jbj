/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, Expr}
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import scala.Some
import de.leanovate.jbj.runtime.context.Context

case class CallMethodReferableExpr(instanceExpr: Expr, methodName: Name, parameters: List[Expr])
  extends CallReferableExpr {
  def call(implicit ctx: Context): PAny = instanceExpr.eval.asVal match {
    case instance: ObjectVal =>
      instance.pClass.invokeMethod(ctx, Some(instance), methodName.evalName, parameters.map(ExprParam.apply))
    case _ =>
      throw new FatalErrorJbjException("Call to a member function %s() on a non-object".format(methodName.evalName))
  }
}
