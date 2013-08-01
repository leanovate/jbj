package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, NamespaceName, Reference, Expr}
import de.leanovate.jbj.runtime.{Value, Context}

case class CallFunctionReference(functionName: Name, parameters: List[Expr]) extends Reference {
  override def eval(ctx: Context) = ctx.findFunction(functionName.evalNamespaceName(ctx)).map {
    func => func.call(ctx, position, parameters.map(_.eval(ctx))) match {
      case Left(value) => value
      case Right(valueRef) => valueRef.value
    }
  }.getOrElse(throw new IllegalArgumentException("No such function: " + functionName))

  override def assign(ctx: Context, value: Value) {
    ctx.findFunction(functionName.evalNamespaceName(ctx)).map {
      func => func.call(ctx, position, parameters.map(_.eval(ctx))) match {
        case Right(valueRef) => valueRef.value = value
        case Left(_) => throw new RuntimeException("Function does not have reference result")
      }
    }.getOrElse(throw new IllegalArgumentException("No such function: " + functionName))
  }
}
