package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.{Reference, Context}
import de.leanovate.jbj.runtime.value.{PVal, PVar, PAny}

trait ReferableExpr extends Expr {
  def evalRef(implicit ctx: Context): Reference

  def evalVar(implicit ctx: Context): PAny

  def assignVar(pAny: PAny)(implicit ctx: Context)
}
