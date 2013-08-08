package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.Value

case class DefaultCaseBlock(stmts: List[Stmt]) extends SwitchCase {
  def matches(value: Value)(implicit ctx: Context) = true
}
