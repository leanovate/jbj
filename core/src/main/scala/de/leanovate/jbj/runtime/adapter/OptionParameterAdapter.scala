package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PVal, PAny}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.{Expr, NodePosition}

case class OptionParameterAdapter[T, S <: PAny](converter: Converter[T, S]) extends ParameterAdapter[Option[T]] {
  override def requiredCount = 0

  override def adapt(parameters: List[Expr])(implicit ctx: Context) =
    parameters match {
      case head :: tail => Some(Some(converter.toScalaWithConversion(head)), tail)
      case Nil => Some(None, Nil)
    }
}
