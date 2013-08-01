package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{Context, PFunction, Value}
import de.leanovate.jbj.ast.NodePosition

case class BuildinFunction3(name: String, impl: PartialFunction[(Option[Value], Option[Value], Option[Value]), Value])
  extends PFunction {
  def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]) = parameters match {
    case param :: Nil => Left(impl.apply((Some(param), None, None)))
    case param1 :: param2 :: Nil => Left(impl.apply((Some(param1), Some(param2), None)))
    case param1 :: param2 :: param3 :: Nil => Left(impl.apply((Some(param1), Some(param2), Some(param3))))
    case _ => Left(impl.apply((None, None, None)))
  }
}
