package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.{VarRef, PAnyVal}

trait Expr extends Node {
  def isDefined(implicit ctx: Context) = !eval.isNull

  def eval(implicit ctx: Context): PAnyVal
}