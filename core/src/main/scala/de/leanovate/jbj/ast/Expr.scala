package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.value.{PAny, PVal}
import de.leanovate.jbj.runtime.context.Context

trait Expr extends Node {
  def isDefined(implicit ctx: Context) = !evalOld.isNull

  def eval(implicit ctx: Context): PAny

  def evalOld(implicit ctx: Context): PVal = eval.asVal
}