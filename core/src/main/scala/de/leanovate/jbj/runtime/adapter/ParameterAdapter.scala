package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

trait ParameterAdapter[T] {
  def requiredCount: Int

  def adapt(parameters: List[Expr])(implicit ctx: Context): Option[(T, List[Expr])]
}
