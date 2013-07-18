package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.{Value, Context}

trait Expr extends Node {
  def eval(ctx: Context): Value
}