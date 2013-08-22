package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

object ContextParameterAdapter extends ParameterAdapter[Context] {
  override def requiredCount = 0

  override def adapt(parameters: List[Expr])(implicit ctx: Context) =
    Some(ctx, parameters)
}
