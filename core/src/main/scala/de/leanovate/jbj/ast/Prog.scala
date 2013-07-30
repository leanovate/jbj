package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.Context
import java.io.PrintStream

case class Prog(stmts: Seq[Stmt]) extends Node {
  val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  def exec(ctx: Context) {
    if (!ctx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(ctx))
      ctx.static.initialized = true
    }

    stmts.foreach(_.exec(ctx))
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    stmts.foreach(_.dump(out, ident + "  "))
  }
}
