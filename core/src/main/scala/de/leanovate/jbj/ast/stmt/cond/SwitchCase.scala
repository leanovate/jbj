package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{Stmt, Value}
import de.leanovate.jbj.exec.Context

trait SwitchCase {
  def stmts:List[Stmt]
  def matches(value: Value, ctx: Context): Boolean
}
