package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr, Expr}
import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import scala.Some
import de.leanovate.jbj.runtime.context.Context

case class CallMethodReferableExpr(instanceExpr: Expr, methodName: Name, parameters: List[Expr]) extends ReferableExpr {
  override def eval(implicit ctx: Context) = callMethod.asVal

  override def evalRef(implicit ctx: Context) = new Reference {
    val result = callMethod

    def asVal = result.asVal

    def asVar = result

    def assign(pAny: PAny) = pAny

    def unset() {
      throw new FatalErrorJbjException("Can't use function return value in write context")
    }
  }

  private def callMethod(implicit ctx: Context): PAny = instanceExpr.evalOld match {
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
