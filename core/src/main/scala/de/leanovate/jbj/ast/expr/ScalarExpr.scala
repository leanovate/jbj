package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.runtime.{Context, Value}
import de.leanovate.jbj.ast.{NodePosition, Expr}

case class ScalarExpr(value:Value) extends Expr {
  def eval(ctx: Context) = value
}
