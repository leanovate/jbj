package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.Value

case class CaseBlock(expr: Expr, stmts: List[Stmt]) extends SwitchCase {
  def matches(value: Value)(implicit ctx: Context) = Value.compare(expr.eval, value) == 0
}
