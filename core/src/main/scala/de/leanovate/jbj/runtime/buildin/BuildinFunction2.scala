package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{Context, PFunction}
import de.leanovate.jbj.ast.{Expr, NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}

case class BuildinFunction2(_name: String, impl: PartialFunction[(Context, NodePosition, Option[Value], Option[Value]), Value]) extends PFunction {
override  def name = NamespaceName(relative = false, _name)

  override def call(parameters: List[Expr])(implicit ctx: Context, callerPosition: NodePosition) = parameters.map(_.eval(ctx)) match {
    case param :: Nil => impl.apply(ctx, callerPosition, Some(param.value), None)
    case param1 :: param2 :: Nil => impl.apply(ctx, callerPosition, Some(param1.value), Some(param2.value))
    case _ => impl.apply(ctx, callerPosition, None, None)
  }
}
