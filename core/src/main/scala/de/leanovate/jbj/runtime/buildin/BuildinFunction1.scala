package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{Value, Function, Context}
import de.leanovate.jbj.ast.FilePosition

case class BuildinFunction1(name: String, impl: PartialFunction[Option[Value], Value]) extends Function {
  def call(ctx: Context, callerPosition:FilePosition, parameters: List[Value]) = parameters match {
    case param :: Nil => impl.apply(Some(param))
    case _ => impl.apply(None)
  }
}
