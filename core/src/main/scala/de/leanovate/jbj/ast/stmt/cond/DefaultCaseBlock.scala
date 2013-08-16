package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.PAnyVal

case class DefaultCaseBlock(stmts: List[Stmt]) extends SwitchCase {
  def matches(value: PAnyVal)(implicit ctx: Context) = true
}
