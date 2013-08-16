package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.value.PVar

trait StaticContext {
  var initialized = false

  def findVariable(name: String)(implicit position: NodePosition):Option[PVar]

  def defineVariable(name: String, valueRef: PVar)(implicit position: NodePosition)

  def undefineVariable(name: String)
}
