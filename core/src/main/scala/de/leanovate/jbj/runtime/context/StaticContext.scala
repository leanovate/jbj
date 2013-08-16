package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.value.VarRef

trait StaticContext {
  var initialized = false

  def findVariable(name: String)(implicit position: NodePosition):Option[VarRef]

  def defineVariable(name: String, valueRef: VarRef)(implicit position: NodePosition)

  def undefineVariable(name: String)
}
