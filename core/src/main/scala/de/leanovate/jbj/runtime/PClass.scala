package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.{NamespaceName, NodePosition}

trait PClass {
  def name: NamespaceName

  def newInstance(ctx: Context, callerPosition: NodePosition, parameters: List[Value])

  def invokemethod(ctx: Context, callerPosition: NodePosition, instance: Value,
                   parameters: List[Value]): Either[Value, ValueRef]
}