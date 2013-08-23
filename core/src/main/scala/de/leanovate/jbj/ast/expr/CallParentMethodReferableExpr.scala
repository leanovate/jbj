package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Name}
import de.leanovate.jbj.runtime.context.{MethodContext, Context}
import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class CallParentMethodReferableExpr(methodName: Name, parameters: List[Expr]) extends CallReferableExpr {
  def call(implicit ctx: Context): PAny = ctx match {
    case MethodContext(instance, _, _) =>
      instance.pClass.superClass.map {
        parent =>
          parent.invokeMethod(ctx, Some(instance), methodName.evalName, parameters)
      }.getOrElse {
        throw new FatalErrorJbjException("Cannot access parent:: when current class scope has no parent")
      }
    case _ =>
      throw new FatalErrorJbjException("Cannot access parent:: when no class scope is active")
  }
}