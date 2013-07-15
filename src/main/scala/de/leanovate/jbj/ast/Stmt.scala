package de.leanovate.jbj.ast

import de.leanovate.jbj.exec.{ExecResult, Context}

trait Stmt extends Node {
  def exec(ctx: Context): ExecResult
}
