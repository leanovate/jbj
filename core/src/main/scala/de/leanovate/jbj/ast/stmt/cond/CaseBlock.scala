package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.PAnyVal

case class CaseBlock(expr: Expr, stmts: List[Stmt]) extends SwitchCase {
  def matches(value: PAnyVal)(implicit ctx: Context) = PAnyVal.compare(expr.eval, value) == 0
}
