package de.leanovate.jbj.ast.buildin

import de.leanovate.jbj.ast.{Value, Function}
import de.leanovate.jbj.exec.Context

case class BuildinFunction1(name: String, impl: Value => Value) extends Function {
  def call(ctx: Context, parameters: List[Value]) = parameters match {
    case param :: Nil => impl.apply(param)
    case _ => throw new IllegalArgumentException("Invalid number of arguments: " + name)
  }
}
