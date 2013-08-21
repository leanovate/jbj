package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.runtime.context.Context

trait SwitchCase {
  def stmts: List[Stmt]

  def matches(value: PVal)(implicit ctx: Context): Boolean
}
