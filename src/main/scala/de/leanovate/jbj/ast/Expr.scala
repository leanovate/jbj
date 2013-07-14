package de.leanovate.jbj.ast

import de.leanovate.jbj.exec.Context

trait Expr extends Node {
  def eval(ctx: Context): Value
}