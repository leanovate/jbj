package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{Context, Function, Value}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class BuildinFunction3(name: String, impl: (Value, Value, Value) => Value) extends Function {
  def call(ctx: Context, parameters: List[Value]) = parameters match {
    case param :: Nil => impl.apply(param, UndefinedVal, UndefinedVal)
    case param1 :: param2 :: Nil => impl.apply(param1, param2, UndefinedVal)
    case param1 :: param2 :: param3 :: Nil => impl.apply(param1, param2, param3)
    case _ => throw new IllegalArgumentException("Invalid number of arguments: " + name)
  }
}
