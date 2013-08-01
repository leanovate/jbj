package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference, Expr}
import de.leanovate.jbj.runtime.{Value, Context}
import de.leanovate.jbj.runtime.value.{UndefinedVal, ObjectVal}

case class CallMethodReference(instanceExpr: Expr, methodName: Name, parameters: List[Expr]) extends Reference {
  override def eval(ctx: Context) = instanceExpr.eval(ctx) match {
    case instance: ObjectVal =>
      instance.pClass.invokeMethod(ctx, position, instance, methodName.evalName(ctx), parameters.map(_.eval(ctx))) match {
        case Left(value) => value
        case Right(valueRef) => valueRef.value
      }
    case _ =>
      ctx.log.fatal(position, "Call to a member function %s() on a non-object".format(methodName))
      UndefinedVal
  }

  override def assign(ctx: Context, value: Value) {}
}
