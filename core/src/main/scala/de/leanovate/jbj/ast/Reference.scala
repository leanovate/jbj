package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.{Value, Context}

trait Reference extends Expr {
  def assign(value: Value)(implicit ctx: Context)
}
