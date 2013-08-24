package de.leanovate.jbj.core.runtime

import de.leanovate.jbj.core.ast.{Expr, NamespaceName}
import de.leanovate.jbj.core.runtime.value.PAny
import de.leanovate.jbj.core.runtime.context.Context

trait PFunction {
  def name: NamespaceName

  def call(parameters: List[Expr])(implicit callerCtx: Context): PAny
}
