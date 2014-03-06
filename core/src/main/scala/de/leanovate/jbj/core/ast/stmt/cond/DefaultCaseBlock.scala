/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.cond

import de.leanovate.jbj.core.ast.{NodeVisitor, Stmt}
import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.runtime.context.Context

case class DefaultCaseBlock(stmts: List[Stmt]) extends SwitchCase {
  override def isDefault = true

  override def matches(value: PVal)(implicit ctx: Context) = false

  override def accept[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(stmts)
}
