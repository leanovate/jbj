/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.cond

import de.leanovate.jbj.core.ast.{NodeVisitor, Expr, Stmt}
import de.leanovate.jbj.core.runtime.value.PVal
import de.leanovate.jbj.core.runtime.context.Context

case class CaseBlock(expr: Expr, stmts: List[Stmt]) extends SwitchCase {
  def matches(value: PVal)(implicit ctx: Context) = expr.eval.asVal.compare(value) == 0

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(expr).thenChildren(stmts)
}
