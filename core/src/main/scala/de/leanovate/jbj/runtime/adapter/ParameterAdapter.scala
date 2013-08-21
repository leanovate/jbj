package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.{Expr, NodePosition}

trait ParameterAdapter[T] {
  def requiredCount: Int

  def adapt(parameters: List[Expr])(implicit ctx: Context): Option[(T, List[Expr])]
}
