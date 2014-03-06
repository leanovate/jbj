/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast

import de.leanovate.jbj.runtime.context.Context

trait DeclStmt extends Stmt {
  def register(implicit ctx: Context)
}

object DeclStmt {
  def collect(nodes: Node*) = {
    nodes.flatMap(_.accept[DeclStmt](new NodeVisitor[DeclStmt] {
      def visit = {
        case decl: DeclStmt => acceptsNextSibling(decl)
        case _ => acceptsNextChild()
      }
    }).results)
  }
}