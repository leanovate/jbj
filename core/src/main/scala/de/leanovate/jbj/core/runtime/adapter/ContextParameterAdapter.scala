package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

object ContextParameterAdapter extends ParameterAdapter[Context] {
  override def requiredCount = 0

  override def adapt(parameters: List[Expr])(implicit ctx: Context) =
    Some(ctx, parameters)
}
