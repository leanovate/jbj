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
    nodes.flatMap(_.foldWith(new NodeVisitor[Seq[StaticInitializer]] {
      val staticInitializers = Seq.newBuilder[StaticInitializer]

      def result = staticInitializers.result()

      def visit = {
        case classDecl: ClassDeclStmt => acceptsNextSibling
        case funcDecl: FunctionDeclStmt => acceptsNextSibling
        case methodDecl: ClassMethodDecl => acceptsNextSibling
        case staticInitializer: StaticInitializer =>
          staticInitializers += staticInitializer
          acceptsNextChild
        case _ => acceptsNextChild
      }
    }))
  }
}