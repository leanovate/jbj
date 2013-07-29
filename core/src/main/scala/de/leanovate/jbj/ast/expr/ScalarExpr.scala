package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.runtime.{Context, Value}
import de.leanovate.jbj.ast.{FilePosition, Expr}

case class ScalarExpr(position:FilePosition, value:Value) extends Expr {
  def eval(ctx: Context) = value
}
