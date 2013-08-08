package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference, Expr}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.{Value, NullVal, ObjectVal}
import java.io.PrintStream

case class CallMethodReference(instanceExpr: Expr, methodName: Name, parameters: List[Expr]) extends Reference {
  override def eval(implicit ctx: Context) = instanceExpr.eval match {
    case instance: ObjectVal =>
      instance.pClass.invokeMethod(ctx, position, instance, methodName.evalName, parameters.map(_.eval)) match {
        case Left(value) => value
        case Right(valueRef) => valueRef.value
      }
    case _ =>
      ctx.log.fatal(position, "Call to a member function %s() on a non-object".format(methodName))
      NullVal
  }

  override def assign(value: Value)(implicit ctx: Context) {}

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + methodName + " " + position)
    instanceExpr.dump(out, ident + "  ")
    parameters.foreach {
      parameter =>
        parameter.dump(out, ident + "  ")
    }
  }
}
