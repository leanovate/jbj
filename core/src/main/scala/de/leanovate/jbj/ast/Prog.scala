package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.Context

case class Prog(position: FilePosition, stmts: Seq[Stmt]) extends Node {
  val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  def exec(ctx: Context) {
    if (!ctx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(ctx))
      ctx.static.initialized = true
    }

    stmts.foreach(_.exec(ctx))
  }
}
