package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.Value
import de.leanovate.jbj.ast.{NodePosition, Expr}
import de.leanovate.jbj.runtime.Context

case class VarargParameterAdapter[T, S <: Value](converter: Converter[T, S]) extends ParameterAdapter[Seq[T]] {
  override def requiredCount = 0

  override def adapt(parameters: List[Expr])(implicit ctx: Context, position: NodePosition) =
    Some(parameters.map {
      parameter =>
        converter.toScalaWithConversion(parameter.eval)
    }, Nil)
}