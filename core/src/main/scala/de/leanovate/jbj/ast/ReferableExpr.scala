package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{PVal, PVar, PAny}

trait ReferableExpr extends Expr {

  def evalVar(implicit ctx: Context): PAny

  def assignVar(pAny: PAny)(implicit ctx: Context)

  def unsetVar(implicit ctx: Context)
}
