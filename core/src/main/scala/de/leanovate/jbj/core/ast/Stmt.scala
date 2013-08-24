package de.leanovate.jbj.core.ast

import de.leanovate.jbj.core.runtime.ExecResult
import de.leanovate.jbj.core.runtime.context.Context

trait Stmt extends Node with HasNodePosition {
  def exec(implicit ctx: Context): ExecResult
}
