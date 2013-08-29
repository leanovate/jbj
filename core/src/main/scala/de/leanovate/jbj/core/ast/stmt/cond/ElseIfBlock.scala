/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.cond

import de.leanovate.jbj.core.ast.{NodeVisitor, Node, Stmt, Expr}

case class ElseIfBlock(condition: Expr, themStmts: List[Stmt]) extends Node {
  override def visit[R](visitor: NodeVisitor[R])= visitor(this).thenChild(condition).thenChildren(themStmts)

}