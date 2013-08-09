package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}

trait Reference extends Expr {
  final def eval(implicit ctx: Context): Value = evalRef.value

  def evalRef(implicit ctx: Context): ValueOrRef

  def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context)
}
