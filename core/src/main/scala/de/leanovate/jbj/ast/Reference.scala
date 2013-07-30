package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.{Value, Context}

trait Reference extends Expr {
  def assign(ctx: Context, value: Value)
}
