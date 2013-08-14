package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.StringVal

case class BitNotExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = expr.eval match {
    case value: StringVal => ~value
    case value => ~value.toInteger
  }

}
