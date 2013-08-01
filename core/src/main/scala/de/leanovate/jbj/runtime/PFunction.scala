package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.NodePosition

trait PFunction {
  def name: String

  def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]): Either[Value, ValueRef]
}
