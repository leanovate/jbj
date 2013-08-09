package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.value.ValueRef

trait StaticContext {
  var initialized = false

  def findVariable(name: String)(implicit position: NodePosition):Option[ValueRef]

  def defineVariable(name: String, valueRef: ValueRef)(implicit position: NodePosition)

  def undefineVariable(name: String)
}
