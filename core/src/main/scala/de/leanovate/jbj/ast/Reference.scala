package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{ValueRef, ValueOrRef, Value}

trait Reference extends Expr {

  def evalRef(implicit ctx: Context): ValueOrRef

  def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context)
}
