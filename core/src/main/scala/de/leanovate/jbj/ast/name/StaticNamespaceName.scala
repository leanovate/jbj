package de.leanovate.jbj.ast.name

import de.leanovate.jbj.ast.{Name, NamespaceName}
import de.leanovate.jbj.runtime.Context

case class StaticNamespaceName(namespaceName: NamespaceName) extends Name {
  def evalName(ctx: Context) = evalNamespaceName(ctx).toString

  override def evalNamespaceName(ctx: Context) = namespaceName
}
