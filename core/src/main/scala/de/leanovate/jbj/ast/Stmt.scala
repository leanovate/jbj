package de.leanovate.jbj.ast

import de.leanovate.jbj.runtime.{ExecResult, Context}

trait Stmt extends Node {
  def exec(implicit ctx: Context): ExecResult
}
