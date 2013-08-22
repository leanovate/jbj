package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{NodeVisitor, Expr, Stmt}
import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.runtime.context.Context

case class CaseBlock(expr: Expr, stmts: List[Stmt]) extends SwitchCase {
  def matches(value: PVal)(implicit ctx: Context) = expr.eval.asVal.compare(value) == 0

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(expr).thenChildren(stmts)
}
