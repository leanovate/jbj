package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.{NamespaceName, NodePosition}

trait PFunction {
  def name: NamespaceName

  def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]): Either[Value, ValueRef]
}
