/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast

import de.leanovate.jbj.runtime.context.{Context, StaticContext}
import de.leanovate.jbj.core.ast.decl.{ClassMethodDecl, FunctionDeclStmt, ClassDeclStmt}

trait StaticInitializer extends HasNodePosition {
  self: Node =>
  def initializeStatic(staticCtx: StaticContext)(implicit ctx: Context)
}

object StaticInitializer {
  def collect(nodes: Node*) = {
    nodes.flatMap(_.accept[StaticInitializer](new NodeVisitor[StaticInitializer] {
      def visit = {
        case classDecl: ClassDeclStmt => acceptsNextSibling()
        case funcDecl: FunctionDeclStmt => acceptsNextSibling()
        case methodDecl: ClassMethodDecl => acceptsNextSibling()
        case staticInitializer: StaticInitializer => acceptsNextChild(staticInitializer)
        case _ => acceptsNextChild()
      }
    }).results)
  }
}