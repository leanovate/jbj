package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, ReferableExpr, Name}
import de.leanovate.jbj.runtime.value.{PVar, PAny}
import de.leanovate.jbj.runtime.{Reference, Context}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class CallStaticMethodReferableExpr(className: Name, methodName: Name, parameters: List[Expr]) extends ReferableExpr {
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

  override def unsetVar(implicit ctx: Context) {
    evalRef.unset()
  }

  private def callMethod(implicit ctx: Context): PAny = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        pClass.invokeMethod(ctx, position, None, methodName.evalName, parameters)
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }
}
