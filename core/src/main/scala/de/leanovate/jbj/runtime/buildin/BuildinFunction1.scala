package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{PFunction, Context}
import de.leanovate.jbj.ast.{NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.value.Value

case class BuildinFunction1(_name: String, impl: PartialFunction[(Context, NodePosition, Option[Value]), Value]) extends PFunction {
  def name = NamespaceName(relative = false, _name)

  def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]) = parameters match {
    case param :: Nil => impl.apply(ctx, callerPosition, Some(param))
    case _ => impl.apply(ctx, callerPosition, None)
  }
}
