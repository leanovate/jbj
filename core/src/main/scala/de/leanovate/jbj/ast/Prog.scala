package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.ExecResult
import java.io.PrintStream
import de.leanovate.jbj.ast.stmt.BlockLike
import de.leanovate.jbj.runtime.context.Context

case class Prog(fileName: String, stmts: Seq[Stmt]) extends Stmt with BlockLike {
  private lazy val staticInitializers = StaticInitializer.collect(this)

  override def exec(implicit ctx: Context): ExecResult = {
    staticInitializers.foreach(_.initializeStatic(ctx.static))
    ctx.static.initialized = true

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
