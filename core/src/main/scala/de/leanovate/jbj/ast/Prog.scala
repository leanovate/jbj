package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.ExecResult
import java.io.PrintStream
import de.leanovate.jbj.ast.stmt.BlockLike
import de.leanovate.jbj.runtime.context.Context

case class Prog(fileName: String, stmts: Seq[Stmt]) extends Stmt with BlockLike {
  private lazy val staticInitializers = StaticInitializer.collect(this)

  private lazy val deprecatedNodes = visit(new Prog.DeprectatedNodeVisitor).results

  override def exec(implicit ctx: Context): ExecResult = {
    staticInitializers.foreach(_.initializeStatic(ctx.static))
    ctx.static.initialized = true

    deprecatedNodes.foreach {
      case (n, pos) =>
        ctx.log.deprecated(pos, n.deprecated.get)
    }

    execStmts(stmts.toList)
  }

  override def toXml =
    <Prog>
      {stmts.map(_.toXml)}
    </Prog>

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    stmts.foreach(_.dump(out, ident + "  "))
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(stmts)
}

object Prog {
  class DeprectatedNodeVisitor extends NodeVisitor[(Node, NodePosition)] {
    var pos :NodePosition = NoNodePosition

    def apply(node: Node) = node match {
      case n :Node with HasNodePosition if n.deprecated.isDefined =>
        pos = n.position
        NextChild((n, pos))
      case n if n.deprecated.isDefined => NextChild((n, pos))
      case n :Node with HasNodePosition =>
        pos = n.position
        NextChild()
      case _ => NextChild()
    }
  }

}