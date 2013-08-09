package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference, Name}
import de.leanovate.jbj.runtime.value.Value
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class CallStaticMethodReference(className: Name, methodName: Name, parameters: List[Expr]) extends Reference {
  def eval(implicit ctx: Context) = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name).map {
      pClass =>
        pClass.invokeMethod(ctx, position, None, methodName.evalName, parameters.map(_.eval)) match {
          case Left(value) => value
          case Right(valueRef) => valueRef.value
        }
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }

  def assign(value: Value)(implicit ctx: Context) {
  }
}
