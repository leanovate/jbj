package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Expr}
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import scala.Some
import de.leanovate.jbj.runtime.context.Context

case class CallMethodReferableExpr(instanceExpr: Expr, methodName: Name, parameters: List[Expr])
  extends CallReferableExpr {
  def call(implicit ctx: Context): PAny = instanceExpr.eval.asVal match {
    case instance: ObjectVal =>
      instance.pClass.invokeMethod(ctx, Some(instance), methodName.evalName, parameters)
    case _ =>
      throw new FatalErrorJbjException("Call to a member function %s() on a non-object".format(methodName.evalName))
  }

  override def toXml =
    <CallMethodReferableExpr>
      <instanceExpr>
        {instanceExpr.toXml}
      </instanceExpr>
      <methodName>
        {methodName.toXml}
      </methodName>
      <parameters>
        {parameters.map {
        _.toXml
      }}
      </parameters>
    </CallMethodReferableExpr>
}
