package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr, Expr}
import de.leanovate.jbj.runtime.{Reference, Context}
import de.leanovate.jbj.runtime.value._
import java.io.PrintStream
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import scala.Some

case class CallMethodReferableExpr(instanceExpr: Expr, methodName: Name, parameters: List[Expr]) extends ReferableExpr {
  override def eval(implicit ctx: Context) = callMethod.asVal

  override def evalRef(implicit ctx: Context) = new Reference {
    val result = callMethod

    def asVal = result.asVal

    def asVar = result

    def assign(pAny: PAny) = pAny

    def unset() {
      throw new FatalErrorJbjException("Can't use function return value in write context")
    }
  }

  override def evalVar(implicit ctx: Context) = evalRef.asVar

  override def assignVar(valueOrRef: PAny)(implicit ctx: Context) {
    evalRef.assign(valueOrRef)
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + methodName + " " + position)
    instanceExpr.dump(out, ident + "  ")
    parameters.foreach {
      parameter =>
        parameter.dump(out, ident + "  ")
    }
  }


  private def callMethod(implicit ctx: Context): PAny = instanceExpr.eval match {
    case instance: ObjectVal =>
      instance.pClass.invokeMethod(ctx, position, Some(instance), methodName.evalName, parameters)
    case _ =>
      throw new FatalErrorJbjException("Call to a member function %s() on a non-object".format(methodName.evalName))
  }
}
