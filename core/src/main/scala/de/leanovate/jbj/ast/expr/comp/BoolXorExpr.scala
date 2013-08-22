package de.leanovate.jbj.ast.expr.comp

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.value.BooleanVal
import de.leanovate.jbj.ast.expr.BinaryExpr
import de.leanovate.jbj.runtime.context.Context

case class BoolXorExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(implicit ctx: Context) = {
    val leftVal = left.eval.asVal.toBool.asBoolean
    val rightVal = left.eval.asVal.toBool.asBoolean

    BooleanVal(leftVal ^ rightVal)
  }
}
