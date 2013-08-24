package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.ast.{Expr, NodePosition}
import de.leanovate.jbj.core.runtime.context.Context

object PositionParameterAdapter extends ParameterAdapter[NodePosition] {
  override def requiredCount = 0

  override def adapt(parameters: List[Expr])(implicit ctx: Context) =
    Some(ctx.currentPosition, parameters)
}
