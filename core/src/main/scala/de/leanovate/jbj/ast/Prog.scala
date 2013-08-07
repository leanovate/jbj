package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.{ExecResult, Context}
import java.io.PrintStream
import de.leanovate.jbj.runtime.exception.JbjException
import de.leanovate.jbj.ast.stmt.BlockLike

case class Prog(fileName:String, stmts: Seq[Stmt]) extends Stmt with BlockLike {
  val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  override def exec(implicit ctx: Context): ExecResult = {
    if (!ctx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(ctx))
      ctx.static.initialized = true
    }

    execStmts(stmts.toList)
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    stmts.foreach(_.dump(out, ident + "  "))
  }
}
