package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, NamespaceName, Reference, Expr}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.value.{ValueOrRef, ValueRef, Value}

case class CallFunctionReference(functionName: Name, parameters: List[Expr]) extends Reference {
  override def evalRef(implicit ctx: Context) = {
    val name = functionName.evalNamespaceName
    ctx.findFunction(name).map {
      func => func.call(ctx, position, parameters.map(_.eval))
    }.getOrElse {
      throw new FatalErrorJbjException("Call to undefined function %s()".format(name.toString))
    }
  }

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {
    val name = functionName.evalNamespaceName
    ctx.findFunction(name).map {
      func => func.call(ctx, position, parameters.map(_.eval)) match {
        case valueRef : ValueRef => valueRef.value = valueOrRef.value
        case _ => throw new RuntimeException("Function does not have reference result")
      }
    }.getOrElse {
      throw new FatalErrorJbjException("Call to undefined function %s()".format(name.toString))
    }
  }
}
