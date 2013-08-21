package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.PVal

case class CaseBlock(expr: Expr, stmts: List[Stmt]) extends SwitchCase {
  def matches(value: PVal)(implicit ctx: Context) = expr.evalOld.compare(value) == 0
}
