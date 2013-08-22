package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

case class VarargParameterAdapter[T, S <: PAny](converter: Converter[T, S]) extends ParameterAdapter[Seq[T]] {
  override def requiredCount = 0

  override def adapt(parameters: List[Expr])(implicit ctx: Context) =
    Some(parameters.map {
      parameter =>
        converter.toScalaWithConversion(parameter)
    }, Nil)
}