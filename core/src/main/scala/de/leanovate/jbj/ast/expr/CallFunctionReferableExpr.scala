package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr, Expr}
import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.context.Context

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
