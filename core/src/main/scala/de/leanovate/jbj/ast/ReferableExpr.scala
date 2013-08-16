package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{VarRef, PAny, PAnyVal}

trait ReferableExpr extends Expr {

  def evalRef(implicit ctx: Context): PAny

  def assignRef(valueOrRef: PAny)(implicit ctx: Context)

  def unsetRef(implicit ctx: Context)
}
