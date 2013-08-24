package de.leanovate.jbj.core.ast

import de.leanovate.jbj.core.runtime.Reference
import de.leanovate.jbj.core.runtime.context.Context

trait ReferableExpr extends Expr {
  override def isDefined(implicit ctx: Context) = evalRef.isDefined

  def evalRef(implicit ctx: Context): Reference
}
