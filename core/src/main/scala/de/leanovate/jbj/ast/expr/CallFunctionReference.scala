package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, NamespaceName, Reference, Expr}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.value.Value

case class CallFunctionReference(functionName: Name, parameters: List[Expr]) extends Reference {
  override def eval(implicit ctx: Context) = {
    val name = functionName.evalNamespaceName
    ctx.findFunction(name).map {
      func => func.call(ctx, position, parameters.map(_.eval)) match {
        case Left(value) => value
        case Right(valueRef) => valueRef.value
      }
    }.getOrElse {
      throw new FatalErrorJbjException("Call to undefined function %s()".format(name.toString))
    }
  }

  override def assign(value: Value)(implicit ctx: Context) {
    val name = functionName.evalNamespaceName
    ctx.findFunction(name).map {
      func => func.call(ctx, position, parameters.map(_.eval)) match {
        case Right(valueRef) => valueRef.value = value
        case Left(_) => throw new RuntimeException("Function does not have reference result")
      }
    }.getOrElse {
      throw new FatalErrorJbjException("Call to undefined function %s()".format(name.toString))
    }
  }
}
