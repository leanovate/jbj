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

case class CallMethodRefExpr(instanceExpr: Expr, methodName: Name, parameters: List[Expr])
  extends CallRefExpr {

  override def call(implicit ctx: Context): PAny = instanceExpr.eval.asVal match {
    case instance: ObjectVal =>
      methodName.evalNameStrict match {
        case Some(name) =>
          instance.pClass.invokeMethod(Some(instance), name, parameters.map(ExprParam.apply))
        case None =>
          throw new FatalErrorJbjException("Method name must be a string")
      }
    case _ =>
      throw new FatalErrorJbjException("Call to a member function %s() on a non-object".format(methodName.evalName))
  }

  override def phpStr = instanceExpr.phpStr + "->" + methodName.phpStr + parameters.map(_.phpStr).mkString("(", ", ", ")")
}
