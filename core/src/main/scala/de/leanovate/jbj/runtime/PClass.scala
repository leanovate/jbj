package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.NodePosition

trait PClass {
  def newInstance(ctx: Context, callerPosition: NodePosition, parameters: List[Value])

  def invokemethod(ctx: Context, callerPosition: NodePosition, instance: Value,
                   parameters: List[Value]): Either[Value, ValueRef]
}