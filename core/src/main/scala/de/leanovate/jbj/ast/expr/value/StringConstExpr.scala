package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.value.StringVal

case class StringConstExpr(value:String) extends Expr {
  def eval(ctx: Context) = StringVal(value)
}
