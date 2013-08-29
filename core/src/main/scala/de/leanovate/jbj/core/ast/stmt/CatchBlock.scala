/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{NodeVisitor, NamespaceName, Stmt, Node}

case class CatchBlock(exceptionName: NamespaceName, variableName: String, stmts: List[Stmt]) extends Node {
  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(stmts)
}
