package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.context.{Context, StaticContext}
import de.leanovate.jbj.ast.stmt.{ClassDeclStmt, ClassMethodDecl, FunctionDeclStmt}

trait StaticInitializer {
  def initializeStatic(staticCtx: StaticContext)(implicit ctx: Context)
}

object StaticInitializer {
  def collect(nodes: Node*) = {
    nodes.flatMap(_.visit[StaticInitializer](new NodeVisitor[StaticInitializer] {
      def apply(node: Node) = {
        node match {
          case classDecl: ClassDeclStmt => NextSibling()
          case funcDecl: FunctionDeclStmt =>            NextSibling()
          case methodDecl: ClassMethodDecl => NextSibling()
          case staticInitializer: StaticInitializer => NextChild(staticInitializer)
          case _ => NextChild()
        }
      }
    }).results)
  }
}