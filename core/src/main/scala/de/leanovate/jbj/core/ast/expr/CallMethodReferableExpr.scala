package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, Expr}
import de.leanovate.jbj.core.runtime.value._
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import scala.Some
import de.leanovate.jbj.core.runtime.context.Context

case class CallMethodReferableExpr(instanceExpr: Expr, methodName: Name, parameters: List[Expr])
  extends CallReferableExpr {
  def call(implicit ctx: Context): PAny = instanceExpr.eval.asVal match {
    case instance: ObjectVal =>
      instance.pClass.invokeMethod(ctx, Some(instance), methodName.evalName, parameters)
    case _ =>
      throw new FatalErrorJbjException("Call to a member function %s() on a non-object".format(methodName.evalName))
  }
}
