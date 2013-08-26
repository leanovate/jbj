package de.leanovate.jbj.core.ast.name

import de.leanovate.jbj.core.ast.Name
import de.leanovate.jbj.core.runtime.context.Context

case class StaticName(name: String) extends Name {
  override def evalName(implicit ctx: Context) = name
}
