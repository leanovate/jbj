package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{NodeVisitor, Expr}
import de.leanovate.jbj.core.runtime.value.IntegerVal
import de.leanovate.jbj.core.runtime.context.Context

case class PrintExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    ctx.out.print(expr.eval.toOutput)
    IntegerVal(1)
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(expr)
}
