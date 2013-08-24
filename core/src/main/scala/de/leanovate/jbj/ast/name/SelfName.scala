package de.leanovate.jbj.ast.name

import de.leanovate.jbj.ast.Name
import de.leanovate.jbj.runtime.context.{MethodContext, Context}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

object SelfName extends Name {
  override def evalName(implicit ctx: Context) = evalNamespaceName.toString

  override def evalNamespaceName(implicit ctx: Context) = ctx match {
    case MethodContext(instance, pClass, _, _) =>
      pClass.name
    case _ =>
      throw new FatalErrorJbjException("Cannot access self:: when no class scope is active")
  }

  def toXml = <selfName/>
}
