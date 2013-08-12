package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{Value, ValueOrRef}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.NodePosition

case class DefaultParamterAdapter[T](converter: Converter[T, _ <: Value], default: Option[T] = None)
  extends ParameterAdapter[T] {

  override def adapt(parameters: List[ValueOrRef])(implicit ctx: Context, position: NodePosition) =
    parameters match {
      case head :: tail => Some(converter.toScalaWithConversion(head.value), tail)
      case Nil if default.isDefined => Some(default.get, Nil)
      case Nil => None
    }
}
