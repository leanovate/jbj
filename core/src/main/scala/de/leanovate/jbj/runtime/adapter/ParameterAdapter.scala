package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.ValueOrRef
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.NodePosition

trait ParameterAdapter[T] {
  def adapt(parameters: List[ValueOrRef])(implicit ctx: Context, position: NodePosition): Option[(T, List[ValueOrRef])]
}
