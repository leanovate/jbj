package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{Context, Function, Value}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class BuildinFunction3(name: String, impl: PartialFunction[(Option[Value], Option[Value], Option[Value]), Value])
  extends Function {
  def call(ctx: Context, parameters: List[Value]) = parameters match {
    case param :: Nil => impl.apply((Some(param), None, None))
    case param1 :: param2 :: Nil => impl.apply((Some(param1), Some(param2), None))
    case param1 :: param2 :: param3 :: Nil => impl.apply((Some(param1), Some(param2), Some(param3)))
    case _ => impl.apply((None, None, None))
  }
}
