package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{NodeVisitor, Expr}
import de.leanovate.jbj.runtime.value.BooleanVal
import de.leanovate.jbj.runtime.context.Context

case class IsSetExpr(parameters: List[Expr]) extends Expr {
  override def eval(implicit ctx: Context) = BooleanVal(parameters.forall(_.isDefined))

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(parameters)
}
