package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.NodePosition

object RequiredValueParameterAdapter extends ParameterAdapter[Value] {

  override def adapt(parameters: List[ValueOrRef])(implicit ctx: Context, position: NodePosition) =
    parameters match {
      case head :: tail => Some(head.value, tail)
      case Nil => None
    }
}
