package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.{Value, Context}

case class CaseBlock(expr: Expr, stmts: List[Stmt]) extends SwitchCase {
  def matches(value: Value, ctx: Context) = Value.compare(expr.eval(ctx), value) == 0
}
