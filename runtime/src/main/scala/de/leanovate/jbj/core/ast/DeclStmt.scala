/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast

import de.leanovate.jbj.core.runtime.context.Context

trait DeclStmt extends Stmt {
  def register(implicit ctx: Context)
}

object DeclStmt {
  def collect(nodes: Node*) = {
    nodes.flatMap(_.visit[DeclStmt](new NodeVisitor[DeclStmt] {
      def apply(node: Node) = {
        node match {
          case decl: DeclStmt => NextSibling(decl)
          case _ => NextChild()
        }
      }
    }).results)
  }

}