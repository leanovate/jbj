package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.PAny
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

case class DefaultParamterAdapter[T, S <: PAny](converter: Converter[T, S]) extends ParameterAdapter[T] {
  override def requiredCount = 1

  override def adapt(parameters: List[Expr])(implicit ctx: Context) =
    parameters match {
      case head :: tail => Some(converter.toScalaWithConversion(head), tail)
      case Nil => None
    }
}
