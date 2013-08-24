package de.leanovate.jbj.core.ast

import de.leanovate.jbj.core.runtime.value.PAny
import de.leanovate.jbj.core.runtime.context.Context

trait Expr extends Node {
  def isDefined(implicit ctx: Context) = !eval.asVal.isNull

  def eval(implicit ctx: Context): PAny
}