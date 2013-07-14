package de.leanovate.jbj.ast

import de.leanovate.jbj.exec.Context

trait Stmt extends Node {
  def exec(ctx: Context)
}
