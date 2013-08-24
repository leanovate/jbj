package de.leanovate.jbj.core.ast.name

import de.leanovate.jbj.core.ast.Name
import de.leanovate.jbj.core.runtime.context.{StaticMethodContext, MethodContext, Context}
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException

object ParentName extends Name {
  override def evalName(implicit ctx: Context) = evalNamespaceName.toString

  override def evalNamespaceName(implicit ctx: Context) = ctx match {
    case MethodContext(instance, pClass, _, _) =>
      pClass.superClass.map(_.name).getOrElse {
        throw new FatalErrorJbjException("Cannot access parent:: when current class scope has no parent")
      }
    case StaticMethodContext(pClass, _, _) =>
      pClass.superClass.map(_.name).getOrElse {
        throw new FatalErrorJbjException("Cannot access parent:: when current class scope has no parent")
      }
    case _ =>
      throw new FatalErrorJbjException("Cannot access parent:: when no class scope is active")
  }

  def toXml = <parentName/>
}
