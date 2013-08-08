package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.Value

trait SwitchCase {
  def stmts: List[Stmt]

  def matches(value: Value)(implicit ctx: Context): Boolean
}
