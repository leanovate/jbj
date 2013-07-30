package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{Context, PFunction, Value}
import de.leanovate.jbj.runtime.value.UndefinedVal
import de.leanovate.jbj.ast.NodePosition

case class BuildinFunction2(name: String, impl: (Value, Value) => Value) extends PFunction {
  def call(ctx: Context, callerPosition: NodePosition, parameters: List[Value]) = parameters match {
    case param :: Nil => impl.apply(param, UndefinedVal)
    case param1 :: param2 :: Nil => impl.apply(param1, param2)
    case _ => throw new IllegalArgumentException("Invalid number of arguments: " + name)
  }
}
