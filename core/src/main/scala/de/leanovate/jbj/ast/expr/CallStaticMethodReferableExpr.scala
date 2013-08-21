package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, ReferableExpr, Name}
import de.leanovate.jbj.runtime.value.{PVar, PAny}
import de.leanovate.jbj.runtime.{Reference}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context

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

  private def callMethod(implicit ctx: Context): PAny = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        pClass.invokeMethod(ctx, None, methodName.evalName, parameters)
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }
}
