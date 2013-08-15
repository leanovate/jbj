package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{Value, ValueOrRef}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.{Expr, NodePosition}

case class OptionParameterAdapter[T, S <: Value](converter: Converter[T, S]) extends ParameterAdapter[Option[T]] {
  override def requiredCount = 0

  override def adapt(parameters: List[Expr])(implicit ctx: Context, position: NodePosition) =
    parameters match {
      case head :: tail => Some(Some(converter.toScalaWithConversion(head)), tail)
      case Nil => Some(None, Nil)
    }
}
