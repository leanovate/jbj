package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.ValueOrRef
import de.leanovate.jbj.ast.NodePosition

object ContextParameterAdapter extends ParameterAdapter[Context] {
  def adapt(parameters: List[ValueOrRef])(implicit ctx: Context, position: NodePosition) = Some(ctx, parameters)
}
