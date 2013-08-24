package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, Expr}
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.value.PAny
import de.leanovate.jbj.core.runtime.context.Context

case class CallFunctionReferableExpr(functionName: Name, parameters: List[Expr]) extends CallReferableExpr {
  def call(implicit ctx: Context): PAny = {
    val name = functionName.evalNamespaceName
    ctx.findFunction(name).map {
      func => func.call(parameters)
    }.getOrElse {
      throw new FatalErrorJbjException("Call to undefined function %s()".format(name.toString))
    }
  }

  override def toXml =
    <CallFunctionReferableExpr>
      <functionName>
        {functionName.toXml}
      </functionName>
      <parameters>
        {parameters.map {
        _.toXml
      }}
      </parameters>
    </CallFunctionReferableExpr>
}
