package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.value.ValueOrRef
import de.leanovate.jbj.runtime.Context

object PositionParameterAdapter extends ParameterAdapter[NodePosition] {
  def adapt(parameters: List[ValueOrRef])(implicit ctx: Context, position: NodePosition) = Some(position, parameters)
}
