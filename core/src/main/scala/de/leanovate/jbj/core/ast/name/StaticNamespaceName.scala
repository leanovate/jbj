package de.leanovate.jbj.core.ast.name

import de.leanovate.jbj.core.ast.{Name, NamespaceName}
import de.leanovate.jbj.core.runtime.context.Context

case class StaticNamespaceName(namespaceName: NamespaceName) extends Name {
  override def evalName(implicit ctx: Context) = evalNamespaceName.toString

  override def evalNamespaceName(implicit ctx: Context) = namespaceName
}
