package de.leanovate.jbj.core.ast.stmt.cond

import de.leanovate.jbj.core.ast.{Node, Stmt}
import de.leanovate.jbj.core.runtime.value.PVal
import de.leanovate.jbj.core.runtime.context.Context

trait SwitchCase extends Node {
  def stmts: List[Stmt]

  def matches(value: PVal)(implicit ctx: Context): Boolean
}
