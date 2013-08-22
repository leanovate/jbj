package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.{Expr, NamespaceName}
import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.context.Context

trait PFunction {
  def name: NamespaceName

  def call(parameters: List[Expr])(implicit callerCtx: Context): PAny
}
