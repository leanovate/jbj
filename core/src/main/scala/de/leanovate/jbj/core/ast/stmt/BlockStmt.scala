/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{BlockLike, NodeVisitor, Stmt}
import de.leanovate.jbj.runtime.context.Context

case class BlockStmt(stmts: List[Stmt]) extends Stmt with BlockLike {
  override def exec(implicit ctx: Context) = {
    execStmts(stmts)
  }

  override def accept[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(stmts)
}