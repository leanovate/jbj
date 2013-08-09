package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference, Name}
import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class CallStaticMethodReference(className: Name, methodName: Name, parameters: List[Expr]) extends Reference {
  def evalRef(implicit ctx: Context) = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        pClass.invokeMethod(ctx, position, None, methodName.evalName, parameters.map(_.eval))
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }

  def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {
  }
}
