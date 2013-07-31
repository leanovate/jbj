package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{NamespaceName, Reference, Expr}
import de.leanovate.jbj.runtime.{Value, Context}

case class CallFunctionReference(functionName: NamespaceName, parameters: List[Expr]) extends Reference {
  override def eval(ctx: Context) = ctx.findFunction(functionName).map {
    func => func.call(ctx.global, position, parameters.map(_.eval(ctx)))
  }.getOrElse(throw new IllegalArgumentException("No such function: " + functionName))

  override def assign(ctx: Context, value: Value) {}
}
