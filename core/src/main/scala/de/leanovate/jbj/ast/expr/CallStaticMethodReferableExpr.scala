package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, ReferableExpr, Name}
import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class CallStaticMethodReferableExpr(className: Name, methodName: Name, parameters: List[Expr]) extends ReferableExpr {
  override def eval(implicit ctx: Context) = callMethod.value

  override def evalRef(implicit ctx: Context) = callMethod

  override def assignRef(valueOrRef: PAny)(implicit ctx: Context) {
  }

  override def unsetRef(implicit ctx: Context) {
    throw new FatalErrorJbjException("Can't use function return value in write context")
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
