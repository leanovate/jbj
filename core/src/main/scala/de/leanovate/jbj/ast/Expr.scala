package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.value.{PAny, PVal}
import de.leanovate.jbj.runtime.context.Context

trait Expr extends Node {
  def isDefined(implicit ctx: Context) = !eval.asVal.isNull

  def eval(implicit ctx: Context): PAny
}