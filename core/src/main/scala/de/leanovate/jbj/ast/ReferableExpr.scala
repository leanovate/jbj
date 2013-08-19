package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.{Reference, Context}
import de.leanovate.jbj.runtime.value.{PVal, PVar, PAny}

trait ReferableExpr extends Expr {

  def evalRef(implicit ctx: Context): Reference = new Reference {
    def asVar = evalVar

    def assign(pAny: PAny) = {
      assignVar(pAny)
      pAny
    }

    def unset() {
      unsetVar
    }
  }

  def evalVar(implicit ctx: Context): PAny

  def assignVar(pAny: PAny)(implicit ctx: Context)

  def unsetVar(implicit ctx: Context)
}
