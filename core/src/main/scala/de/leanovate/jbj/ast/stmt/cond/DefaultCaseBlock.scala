package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{NodeVisitor, Stmt}
import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.runtime.context.Context

case class DefaultCaseBlock(stmts: List[Stmt]) extends SwitchCase {
  def matches(value: PVal)(implicit ctx: Context) = true

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(stmts)
}
