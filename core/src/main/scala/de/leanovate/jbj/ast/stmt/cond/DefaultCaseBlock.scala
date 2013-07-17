package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{Value, Stmt}
import de.leanovate.jbj.exec.Context

case class DefaultCaseBlock(stmts:List[Stmt]) extends SwitchCase {
  def matches(value: Value, ctx: Context) = true
}
