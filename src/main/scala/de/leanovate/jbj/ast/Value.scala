package de.leanovate.jbj.ast

import de.leanovate.jbj.ast.value.NumericVal
import de.leanovate.jbj.exec.Context

trait Value extends Expr {
  def eval(ctx: Context): Value = this

  def toNumeric: NumericVal
}
