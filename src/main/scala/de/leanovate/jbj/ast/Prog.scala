package de.leanovate.jbj.ast

import de.leanovate.jbj.exec.Context

case class Prog(stmts: List[Stmt]) extends Node {
  def exec(ctx: Context) {
    stmts.foreach(_.exec(ctx))
  }
}
