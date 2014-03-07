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
    nodes.flatMap(_.foldWith(new NodeVisitor[Seq[DeclStmt]] {
      val declStmts = Seq.newBuilder[DeclStmt]

      def result = declStmts.result()

      def visit = {
        case decl: DeclStmt =>
          declStmts += decl
          acceptsNextSibling
        case _ => acceptsNextChild
      }
    }))
  }
}