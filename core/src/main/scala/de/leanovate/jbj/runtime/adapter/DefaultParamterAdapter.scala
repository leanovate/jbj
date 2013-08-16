package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PAnyVal
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.{Expr, NodePosition}

case class DefaultParamterAdapter[T, S <: PAnyVal](converter: Converter[T, S])
  extends ParameterAdapter[T] {
  override def requiredCount = 1

  override def adapt(parameters: List[Expr])(implicit ctx: Context, position: NodePosition) =
    parameters match {
      case head :: tail => Some(converter.toScalaWithConversion(head), tail)
      case Nil => None
    }
}
