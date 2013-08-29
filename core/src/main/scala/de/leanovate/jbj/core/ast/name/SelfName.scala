package de.leanovate.jbj.core.ast.name

import de.leanovate.jbj.core.ast.Name
import de.leanovate.jbj.core.runtime.context.{ClassContext, StaticMethodContext, MethodContext, Context}
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException

object SelfName extends Name {
  override def evalName(implicit ctx: Context) = evalNamespaceName.toString

  override def evalNamespaceName(implicit ctx: Context) = ctx match {
    case MethodContext(instance, pMethod, _) =>
      pMethod.declaringClass.name
    case StaticMethodContext(pMethod, _) =>
      pMethod.declaringClass.name
    case ClassContext(pClass, _, _) =>
      pClass.name
    case _ =>
      throw new FatalErrorJbjException("Cannot access self:: when no class scope is active")
  }
}
