package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{Value, PFunction, Context}
import de.leanovate.jbj.ast.FilePosition

case class BuildinFunction1(name: String, impl: PartialFunction[Option[Value], Value]) extends PFunction {
  def call(ctx: Context, callerPosition: FilePosition, parameters: List[Value]) = parameters match {
    case param :: Nil => impl.apply(Some(param))
    case _ => impl.apply(None)
  }
}
