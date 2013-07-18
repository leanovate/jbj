package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{Value, Function, Context}

case class BuildinFunction1(name: String, impl: Value => Value) extends Function {
  def call(ctx: Context, parameters: List[Value]) = parameters match {
    case param :: Nil => impl.apply(param)
    case _ => throw new IllegalArgumentException("Invalid number of arguments: " + name)
  }
}
