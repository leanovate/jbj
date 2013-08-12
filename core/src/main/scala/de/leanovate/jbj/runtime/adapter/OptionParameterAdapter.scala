package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{Value, ValueOrRef}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.NodePosition

case class OptionParameterAdapter[T](converter: Converter[T, _ <: Value]) extends ParameterAdapter[Option[T]] {

  override def adapt(parameters: List[ValueOrRef])(implicit ctx: Context, position: NodePosition) =
    parameters match {
      case head :: tail => Some(Some(converter.toScalaWithConversion(head.value)), tail)
      case Nil => Some(None, Nil)
    }
}
