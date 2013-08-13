package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{PFunction, Context}
import de.leanovate.jbj.ast.{Expr, NamespaceName, NodePosition}
import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}

case class BuildinFunction1(_name: String, impl: PartialFunction[(Context, NodePosition, Option[Value]), Value])
  extends PFunction {
  override def name = NamespaceName(relative = false, _name)

  override def call(parameters: List[Expr])(implicit ctx: Context, callerPosition: NodePosition) =
    parameters.map(_.eval(ctx)) match {
      case param :: Nil => impl.apply(ctx, callerPosition, Some(param.value))
      case _ => impl.apply(ctx, callerPosition, None)
    }
}
