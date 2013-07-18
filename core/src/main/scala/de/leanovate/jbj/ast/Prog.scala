package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.Context

case class Prog(stmts: Seq[Stmt]) extends Node {
  def exec(ctx: Context) {
    stmts.foreach(_.exec(ctx))
  }
}
