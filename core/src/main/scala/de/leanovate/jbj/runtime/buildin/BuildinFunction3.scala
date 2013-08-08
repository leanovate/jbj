package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{Context, PFunction}
import de.leanovate.jbj.ast.{NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.value.Value

case class BuildinFunction3(_name: String, impl: PartialFunction[(Context, NodePosition, Option[Value], Option[Value], Option[Value]), Value])
  extends PFunction {
  def name = NamespaceName(_name)

  def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]) = parameters match {
    case param :: Nil => Left(impl.apply(ctx, callerPosition, Some(param), None, None))
    case param1 :: param2 :: Nil => Left(impl.apply(ctx, callerPosition, Some(param1), Some(param2), None))
    case param1 :: param2 :: param3 :: Nil => Left(impl.apply(ctx, callerPosition, Some(param1), Some(param2), Some(param3)))
    case _ => Left(impl.apply(ctx, callerPosition, None, None, None))
  }
}
