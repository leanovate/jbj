package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{NodeVisitor, Expr}
import de.leanovate.jbj.core.runtime.value.BooleanVal
import de.leanovate.jbj.core.runtime.context.Context

case class IsSetExpr(parameters: List[Expr]) extends Expr {
  override def eval(implicit ctx: Context) = BooleanVal(parameters.forall(_.isDefined))

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(parameters)
}
