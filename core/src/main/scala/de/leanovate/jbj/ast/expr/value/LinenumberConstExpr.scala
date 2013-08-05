package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.IntegerVal

case class LineNumberConstExpr() extends Expr {
  override def eval(implicit ctx: Context) = IntegerVal(position.line)
}
