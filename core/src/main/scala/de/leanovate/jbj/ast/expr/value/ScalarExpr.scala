package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.runtime.context.Context

case class ScalarExpr(value: PVal) extends Expr {
  override def eval(implicit ctx: Context) = value

  override def toXml =
    <ScalarExpr>
      {value.toXml}
    </ScalarExpr>
}
