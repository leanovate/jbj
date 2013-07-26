package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{Value, Context}

case class DefaultCaseBlock(stmts: List[Stmt]) extends SwitchCase {
  def matches(value: Value, ctx: Context) = true
}
