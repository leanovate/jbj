package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.context.Context

trait ReferableExpr extends Expr {
  def evalRef(implicit ctx: Context): Reference
}
