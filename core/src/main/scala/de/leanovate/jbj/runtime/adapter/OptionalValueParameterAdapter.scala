package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.NodePosition

object OptionalValueParameterAdapter extends ParameterAdapter[Option[Value]] {
  override def adapt(parameters: List[ValueOrRef])(implicit ctx: Context, position: NodePosition) =
    parameters match {
      case head :: tail => Some(Some(head.value), tail)
      case Nil => Some(None, Nil)
    }
}
