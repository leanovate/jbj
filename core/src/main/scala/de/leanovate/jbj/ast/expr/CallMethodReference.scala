package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value._
import java.io.PrintStream
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import scala.Some

case class CallMethodReference(instanceExpr: Expr, methodName: Name, parameters: List[Expr]) extends Reference {
  override def eval(implicit ctx: Context) = callMethod.value

  override def evalRef(implicit ctx: Context) = callMethod

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {}

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + methodName + " " + position)
    instanceExpr.dump(out, ident + "  ")
    parameters.foreach {
      parameter =>
        parameter.dump(out, ident + "  ")
    }
  }

  override def unsetRef(implicit ctx:Context) {
    throw new FatalErrorJbjException("Can't use function return value in write context")
  }

  private def callMethod(implicit ctx: Context): ValueOrRef = instanceExpr.eval match {
    case instance: ObjectVal =>
      instance.pClass.invokeMethod(ctx, position, Some(instance), methodName.evalName, parameters)
    case _ =>
      throw new FatalErrorJbjException("Call to a member function %s() on a non-object".format(methodName.evalName))
  }
}
