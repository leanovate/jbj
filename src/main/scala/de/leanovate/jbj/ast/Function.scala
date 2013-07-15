package de.leanovate.jbj.ast

import de.leanovate.jbj.exec.Context

trait Function {
  def name: String

  def call(ctx: Context, parameters: List[Value]): Value
}
