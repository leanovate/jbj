package de.leanovate.jbj.core.ast.expr.comp

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.value.BooleanVal
import de.leanovate.jbj.core.ast.expr.BinaryExpr
import de.leanovate.jbj.core.runtime.context.Context

case class BoolXorExpr(left: Expr, right: Expr) extends BinaryExpr {
  override def eval(implicit ctx: Context) = {
    val leftVal = left.eval.asVal.toBool.asBoolean
    val rightVal = left.eval.asVal.toBool.asBoolean

    BooleanVal(leftVal ^ rightVal)
  }
}
