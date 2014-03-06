/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast

import de.leanovate.jbj.runtime.{NoNodePosition, NodePosition, JbjScript, ExecResult}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.core.ast.decl.{NamespaceDeclStmt, SetNamespaceDeclStmt}
import scala.annotation.tailrec
import de.leanovate.jbj.core.ast.stmt.{HaltCompilerStmt, DeclareDeclStmt, InlineStmt}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class Prog(fileName: String, stmts: Seq[Stmt]) extends Stmt with BlockLike with JbjScript {
  private lazy val staticInitializers = StaticInitializer.collect(this)

  private lazy val deprecatedNodes = accept(new Prog.DeprectatedNodeVisitor).results

  override def exec(implicit ctx: Context): ExecResult = {
    ctx.global.initialize()

    val hasSetNamespace = stmts.exists {
      case _: SetNamespaceDeclStmt => true
      case _: NamespaceDeclStmt => true
      case _ => false
    }
    val hasNamespaceDecl = stmts.exists {
      case _: NamespaceDeclStmt => true
      case _ => false
    }
    if (hasSetNamespace) {
      checkStmtsBeforeSetNamespace(stmts.toList)
    }
    if (hasNamespaceDecl) {
      checkStmtsOutsideNamespace(stmts.toList)
    }

    ctx.global.resetCurrentNamepsace()
    staticInitializers.foreach(_.initializeStatic(ctx.static))
    ctx.static.initialized = true

    deprecatedNodes.foreach {
      case (n, pos) =>
        ctx.log.deprecated(pos, n.deprecated.get)
    }

    registerDecls
    ctx.global.resetCurrentNamepsace()
    execStmts(stmts.toList)
  }

  override def accept[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(stmts)

  @tailrec
  private def checkStmtsOutsideNamespace(stmts: List[Stmt])(implicit ctx: Context) {
    stmts match {
      case (_: InlineStmt) :: tail => checkStmtsOutsideNamespace(tail)
      case (_: NamespaceDeclStmt) :: tail => checkStmtsOutsideNamespace(tail)
      case (_: SetNamespaceDeclStmt) :: tail =>
      case (_: DeclareDeclStmt) :: tail => checkStmtsOutsideNamespace(tail)
      case (_: HaltCompilerStmt) :: tail => checkStmtsOutsideNamespace(tail)
      case stmt :: tail =>
        ctx.currentPosition = stmt.position
        throw new FatalErrorJbjException("No code may exist outside of namespace {}")
      case Nil =>
    }
  }

  @tailrec
  private def checkStmtsBeforeSetNamespace(stmts: List[Stmt])(implicit ctx: Context) {
    stmts match {
      case (_: InlineStmt) :: tail => checkStmtsBeforeSetNamespace(tail)
      case (_: NamespaceDeclStmt) :: tail =>
      case (_: SetNamespaceDeclStmt) :: tail =>
      case (_: DeclareDeclStmt) :: tail =>
      case stmt :: tail =>
        ctx.currentPosition = stmt.position
        throw new FatalErrorJbjException("Namespace declaration statement has to be the very first statement in the script")
      case Nil =>
    }
  }
}

object Prog {

  class DeprectatedNodeVisitor extends NodeVisitor[(Node, NodePosition)] {
    var pos: NodePosition = NoNodePosition

    def visit = {
      case n: Node with HasNodePosition if n.deprecated.isDefined =>
        pos = n.position
        acceptsNextChild((n, pos))
      case n if n.deprecated.isDefined => acceptsNextChild((n, pos))
      case n: Node with HasNodePosition =>
        pos = n.position
        acceptsNextChild()
      case _ => acceptsNextChild()
    }
  }

}