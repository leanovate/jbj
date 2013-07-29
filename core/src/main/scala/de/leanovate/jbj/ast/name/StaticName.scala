package de.leanovate.jbj.ast.name

import de.leanovate.jbj.ast.Name
import de.leanovate.jbj.runtime.Context

case class StaticName(name: String) extends Name {
  def evalName(ctx: Context) = name
}
