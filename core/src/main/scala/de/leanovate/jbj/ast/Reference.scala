package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.Value

trait Reference extends Expr {
  def assign(value: Value)(implicit ctx: Context)
}
