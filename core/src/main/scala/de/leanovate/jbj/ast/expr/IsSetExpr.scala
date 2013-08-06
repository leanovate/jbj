package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.BooleanVal

case class IsSetExpr(parameters: List[Expr]) extends Expr {
  override def eval(implicit ctx: Context) = BooleanVal(parameters.forall(_.isDefined))
}
