package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

trait ParameterAdapter[T] {
  def requiredCount: Int

  def adapt(parameters: List[Expr])(implicit ctx: Context): Option[(T, List[Expr])]
}
