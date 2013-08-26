package de.leanovate.jbj.core.ast.name

import de.leanovate.jbj.core.ast.Name
import de.leanovate.jbj.core.runtime.context.{StaticMethodContext, MethodContext, Context}
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException

object SelfName extends Name {
  override def evalName(implicit ctx: Context) = evalNamespaceName.toString

  override def evalNamespaceName(implicit ctx: Context) = ctx match {
    case MethodContext(instance, pClass, _, _) =>
      pClass.name
    case StaticMethodContext(pClass, _, _) =>
      pClass.name
    case _ =>
      throw new FatalErrorJbjException("Cannot access self:: when no class scope is active")
  }
}
