package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.{Expr, NodePosition}

object ContextParameterAdapter extends ParameterAdapter[Context] {
  override def requiredCount = 0

  override def adapt(parameters: List[Expr])(implicit ctx: Context, position: NodePosition) =
    Some(ctx, parameters)
}
