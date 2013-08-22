package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.context.Context

case class DefaultParamterAdapter[T, S <: PAny](converter: Converter[T, S]) extends ParameterAdapter[T] {
  override def requiredCount = 1

  override def adapt(parameters: List[Expr])(implicit ctx: Context) =
    parameters match {
      case head :: tail => Some(converter.toScalaWithConversion(head), tail)
      case Nil => None
    }
}
