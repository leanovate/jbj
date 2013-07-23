package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.runtime.{Context, Value}
import de.leanovate.jbj.ast.Expr

case class ArrayGetExpr(arrayExpr: Expr, index: Value) extends Expr {
  def eval(ctx: Context) = {
    val arrayVal = arrayExpr.eval(ctx)

    arrayVal
  }
}
