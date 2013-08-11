package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference, Name}
import de.leanovate.jbj.runtime.value.ValueOrRef
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class CallStaticMethodReference(className: Name, methodName: Name, parameters: List[Expr]) extends Reference {
  override def eval(implicit ctx: Context) = callMethod.value

  override def evalRef(implicit ctx: Context) = callMethod

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {
  }

  override def unsetRef(implicit ctx: Context) {
    throw new FatalErrorJbjException("Can't use function return value in write context")
  }

  private def callMethod(implicit ctx: Context): ValueOrRef = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        pClass.invokeMethod(ctx, position, None, methodName.evalName, parameters)
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }
}
