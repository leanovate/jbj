package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference, Expr}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.value.{ValueOrRef, Value, NullVal, ObjectVal}
import java.io.PrintStream

case class CallMethodReference(instanceExpr: Expr, methodName: Name, parameters: List[Expr]) extends Reference {
  override def evalRef(implicit ctx: Context) = instanceExpr.eval match {
    case instance: ObjectVal =>
      instance.pClass.invokeMethod(ctx, position, Some(instance), methodName.evalName, parameters.map(_.eval))
    case _ =>
      ctx.log.fatal(position, "Call to a member function %s() on a non-object".format(methodName.evalName))
      NullVal
  }

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {}

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + methodName + " " + position)
    instanceExpr.dump(out, ident + "  ")
    parameters.foreach {
      parameter =>
        parameter.dump(out, ident + "  ")
    }
  }
}
